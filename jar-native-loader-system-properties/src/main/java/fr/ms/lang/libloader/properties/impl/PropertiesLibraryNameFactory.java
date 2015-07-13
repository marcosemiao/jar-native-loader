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
package fr.ms.lang.libloader.properties.impl;

import fr.ms.lang.libloader.impl.JarNativeLoader.DefaultLibraryNameFactory;
import fr.ms.lang.message.MessagePropertiesFormat;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
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
