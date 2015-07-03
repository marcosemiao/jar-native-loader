import java.util.Enumeration;
import java.util.Properties;

public class Test {

    public static void main(final String[] args) {
	final Properties p = System.getProperties();
	final Enumeration<Object> keys = p.keys();
	while (keys.hasMoreElements()) {
	    final String key = (String) keys.nextElement();
	    final String value = (String) p.get(key);
	    System.out.println(key + ": " + value);
	}
    }
}
