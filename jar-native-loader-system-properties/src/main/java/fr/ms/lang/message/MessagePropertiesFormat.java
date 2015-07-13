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
package fr.ms.lang.message;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 *
 * @author Marco Semiao
 *
 */
public class MessagePropertiesFormat extends Format {

	private static final long serialVersionUID = 1L;

	private final String input;

	public MessagePropertiesFormat(final String input) {
		this.input = input;
	}

	@Override
	public StringBuffer format(final Object obj, final StringBuffer toAppendTo, final FieldPosition pos) {

		if (obj instanceof Properties) {
			final Properties props = (Properties) obj;
			final Pattern p = Pattern.compile("\\{(.*?)\\}");
			final Matcher m = p.matcher(input);
			while (m.find()) {
				final String clef = m.group(1);
				final String valeur = props.getProperty(clef);
				final String valeurQuote = Matcher.quoteReplacement(valeur);
				m.appendReplacement(toAppendTo, valeurQuote);
			}
			m.appendTail(toAppendTo);
		}

		return toAppendTo;
	}

	@Override
	public Object parseObject(final String source, final ParsePosition pos) {
		throw new UnsupportedOperationException();
	}

	public static String format(final String pattern) {
		final Format temp = new MessagePropertiesFormat(pattern);
		final Properties p = System.getProperties();
		return temp.format(p);
	}
}
