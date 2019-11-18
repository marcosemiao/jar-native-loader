/*
 * Copyright 2015 Marco Semiao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package fr.ms.lang.libloader.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.ms.lang.libloader.NativeLoader;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class JarNativeLoader implements NativeLoader {

	private static Logger LOG = Logger.getLogger(JarNativeLoader.class.getName());

	private static final File TMPDIR = new File(System.getProperty("java.io.tmpdir"));

	private final LibraryNameFactory libraryNameFactory;

	public static NativeLoader getInstance() {
		return Holder.instance;
	}

	private final static class Holder {
		private final static NativeLoader instance = new JarNativeLoader();
	}

	public JarNativeLoader() {
		this(new DefaultLibraryNameFactory());
	}

	public JarNativeLoader(final LibraryNameFactory libraryNameFactory) {
		this.libraryNameFactory = libraryNameFactory;
	}

	public void load(final String filename) {
		try {
			loadDescriptor(filename, false);
			loadResource(filename);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void loadLibrary(final String libname) {
		try {
			final String filename = libraryNameFactory.mapLibraryName(libname);

			if (LOG.isLoggable(Level.FINE)) {
				LOG.fine("Resolution du chemin : " + libname + " en : " + filename);
			}

			loadDescriptor(filename, true);
			loadResource(filename);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	private InputStream openResource(final String resourceName) {
		final InputStream is = JarNativeLoader.class.getResourceAsStream(resourceName);
		return is;
	}

	private void loadDescriptor(final String filename, final boolean isLoadLibrary) throws IOException {
		final String filenameDescriptor = filename + ".desc";

		final InputStream is = openResource(filenameDescriptor);

		if (is == null) {
			return;
		}

		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Fichier de description trouvé : " + filenameDescriptor);
		}
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(is));
			String libName;
			while ((libName = reader.readLine()) != null) {
				if (isLoadLibrary) {
					loadLibrary(libName);
				} else {
					load(libName);
				}
			}
			reader.close();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	public void loadResource(final String path) throws IOException {

		final InputStream is = openResource(path);
		if (is == null) {
			throw new UnsatisfiedLinkError("Resource " + path + " not found");
		}

		File nativeFile = null;
		try {
			nativeFile = new File(TMPDIR, path);
			nativeFile.delete();
			if (nativeFile.exists()) {
				if (LOG.isLoggable(Level.INFO)) {
					LOG.fine("Impossible de supprimer le fichier temporaire " + nativeFile
							+ " - Il doit surement etre deja utilisé");
				}
			} else {
				createFile(is, nativeFile);
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine(
							"Copie de la librairie " + path + " du classpath vers un fichier temporaire " + nativeFile);
				}
			}

			System.load(nativeFile.getAbsolutePath());

		} finally {
			if (nativeFile != null && nativeFile.exists()) {
				nativeFile.delete();
			}
		}
	}

	public static File createFile(final InputStream is, final File target) throws IOException {
		if (is == null) {
			throw new NullPointerException("InputStream is null");
		}
		if (target.exists()) {
			target.delete();
		}

		final File directory = target.getParentFile();
		if (directory != null) {
			directory.mkdirs();
		}

		OutputStream outputStream = null;
		try {
			int read;
			final byte[] bytes = new byte[1024];

			outputStream = new FileOutputStream(target);

			while ((read = is.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
			return target;
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}

	public static class DefaultLibraryNameFactory implements LibraryNameFactory {

		public LibraryName createLibraryName(final String path, final String libName) {
			return new LibraryName(path, libName);
		}

		public String mapLibraryName(final String libname) {
			final int lastIndexOf = libname.lastIndexOf("/");
			final int length = libname.length();
			if (libname == null || lastIndexOf < 0 || length < 1) {
				throw new IllegalArgumentException("libname : " + libname);
			}

			final String path = libname.substring(0, lastIndexOf);
			final String libName = libname.substring(lastIndexOf + 1, length);
			final LibraryName libraryName = createLibraryName(path, libName);

			return libraryName.getAbsolutePath();
		}

		public static class LibraryName {

			private final String path;

			private final String libName;

			public LibraryName(final String path, final String libName) {
				this.path = path;
				this.libName = libName;
			}

			public String getPath() {
				return path;
			}

			public String getLibName() {
				return System.mapLibraryName(libName);
			}

			public String getAbsolutePath() {
				return getPath() + "/" + getLibName();
			}
		}
	}
}