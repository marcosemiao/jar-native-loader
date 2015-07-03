package fr.ms.lang.libloader.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;

import fr.ms.lang.libloader.NativeLoader;

public class JarNativeLoader implements NativeLoader {

    private final LibraryNameFactory libraryNameFactory;

    public JarNativeLoader() {
	this(new DefaultLibraryNameFactory());
    }

    public JarNativeLoader(final LibraryNameFactory libraryNameFactory) {
	this.libraryNameFactory = libraryNameFactory;
    }

    @Override
    public void load(final String filename) {
	try {
	    loadResource(filename);
	} catch (final IOException e) {
	    throw new RuntimeException(e);
	}
    }

    @Override
    public void loadLibrary(final String libname) {
	try {
	    final String filename = libraryNameFactory.mapLibraryName(libname);
	    loadResource(filename);
	} catch (final IOException e) {
	    throw new RuntimeException(e);
	}
    }

    public static void loadResource(final String path) throws IOException {
	final InputStream is = JarNativeLoader.class.getResourceAsStream(path);
	if (is != null) {
	    final String suffix = path.substring(path.lastIndexOf("/"), path.length());

	    File nativeFile = null;
	    try {
		nativeFile = File.createTempFile("jarNative", suffix);
		createFile(is, nativeFile);
		System.load(nativeFile.getAbsolutePath());
	    } finally {
		if (nativeFile != null && nativeFile.exists()) {
		    nativeFile.delete();
		}
	    }
	}
    }

    public static File createFile(final InputStream is, final File target) throws IOException {
	if (is == null) {
	    throw new NullPointerException("InputStream is null");
	}
	if (target.exists()) {
	    throw new FileAlreadyExistsException(target.getAbsolutePath());
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

	@Override
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
		return libName;
	    }

	    public String getAbsolutePath() {
		return getPath() + "/" + System.mapLibraryName(getLibName());
	    }
	}
    }
}
