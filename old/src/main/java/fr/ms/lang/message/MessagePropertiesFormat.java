package fr.ms.lang.message;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
