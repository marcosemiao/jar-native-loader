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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
			loadResource(filename);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void loadLibrary(final String libname) {
		try {
			final String filename = libraryNameFactory.mapLibraryName(libname);
			loadResource(filename);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static final File tmpdir = new File(System.getProperty("java.io.tmpdir"));

	public static void loadResource(final String path) throws IOException {

		final InputStream is = JarNativeLoader.class.getResourceAsStream(path);
		if (is == null) {
			throw new UnsatisfiedLinkError("Resource " + path + " not found");
		}

		File nativeFile = null;
		try {
			nativeFile = new File(tmpdir, path);
			createFile(is, nativeFile);
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
