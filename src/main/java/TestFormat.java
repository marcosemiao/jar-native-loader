import java.text.Format;
import java.text.ParseException;
import java.util.Properties;

import fr.ms.lang.message.MessagePropertiesFormat;

public class TestFormat {

    public static void main(final String[] args) throws ParseException {
	final String pattern = "/chacon/{os.arch}/repertoire/{os.name}/transmission.so";
	final Format temp = new MessagePropertiesFormat(pattern);

	final Properties p = System.getProperties();
	final String format = temp.format(p);
	System.out.println(format);

    }

}
