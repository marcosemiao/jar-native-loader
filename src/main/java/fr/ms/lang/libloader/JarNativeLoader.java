package fr.ms.lang.libloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;

public class JarNativeLoader implements NativeLoader {

    private final LibraryNameFactory libraryNameFactory;

    public JarNativeLoader() {
	this(JarNativeLoader.defaultLibraryNameFactory(true));
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

    public static LibraryNameFactory defaultLibraryNameFactory(final boolean osArch) {
	return new DefaultLibraryNameFactory(osArch);
    }

    static class DefaultLibraryNameFactory implements LibraryNameFactory {

	private boolean osArch;

	DefaultLibraryNameFactory() {

	}

	DefaultLibraryNameFactory(final boolean osArch) {
	    this.osArch = osArch;
	}

	@Override
	public String mapLibraryName(final String libname) {
	    final int lastIndexOf = libname.lastIndexOf("/");
	    final int length = libname.length();
	    if (libname == null || lastIndexOf < 0 || length < 1) {
		throw new IllegalArgumentException("libname : " + libname);
	    }
	    final StringBuilder sb = new StringBuilder();

	    final String path = libname.substring(0, lastIndexOf);
	    sb.append(path);
	    sb.append("/");

	    if (osArch) {
		final String os = System.getProperty("os.arch");
		sb.append(os);
		sb.append("/");
	    }
	    final String libName = libname.substring(lastIndexOf + 1, length);
	    final String mapLibraryName = System.mapLibraryName(libName);
	    sb.append(mapLibraryName);
	    return sb.toString();
	}
    }
}
