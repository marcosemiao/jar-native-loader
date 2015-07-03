package fr.ms.lang.libloader.properties.impl;

import fr.ms.lang.libloader.impl.JarNativeLoader.DefaultLibraryNameFactory;
import fr.ms.lang.message.MessagePropertiesFormat;

public class PropertiesLibraryNameFactory extends DefaultLibraryNameFactory {

    @Override
    public LibraryName createLibraryName(final String path, final String libName) {
	return new PropertiesLibraryName(path, libName);
    }

    public static class PropertiesLibraryName extends LibraryName {

	public PropertiesLibraryName(final String path, final String libName) {
	    super(path, libName);
	}

	@Override
	public String getAbsolutePath() {
	    return MessagePropertiesFormat.format(getPath()) + "/" + getLibName();
	}
    }
}
