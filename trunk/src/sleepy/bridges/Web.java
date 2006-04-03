package sleepy.bridges;

import sleep.bridges.*;
import sleep.interfaces.*;
import sleep.runtime.*;
import java.util.*;
import java.net.*;
	
/**
 * "Web" bridge for Sleep
 * -------------------------------
 * This is a generic function library with web-related 
 * functions. Originally designed for use with Sleepy in mind,
 * but thoughtfully built to be generic and reusable.
 *
 * @author Andreas Ravnestad
 * @since 1.0
 */
public class Web implements Loadable {
		
	public Web() {
		
	}

	public boolean scriptLoaded(ScriptInstance s) {
		
		Hashtable env = s.getScriptEnvironment().getEnvironment();

		env.put("&htmlEntities", new HtmlEntities());
		env.put("&removeHtmlTags", new RemoveHtmlTags());
		env.put("&urlEncode", new UrlEncode());
		env.put("&urlDecode", new UrlDecode());

		return true;
	}
	
	public boolean scriptUnloaded(ScriptInstance s) {
		return true;
	}
	
	// htmlEntities(<string>)
	// Converts all special chars to html entities
	private static class HtmlEntities implements Function
	{
		public Scalar evaluate(String name, ScriptInstance script, Stack args)
		{
			String subject = BridgeUtilities.getString(args,"");
			String ignore = BridgeUtilities.getString(args,"");
			return SleepUtils.getScalar(HtmlEntityEncoder.encode(subject, ignore));
		}
	}
	
	// removeHtmlTags(<string>[, excp1, excp2, ...])
	// Removes html tags, except the given exceptions
	private static class RemoveHtmlTags implements Function
	{
		public Scalar evaluate(String name, ScriptInstance script, Stack args)
		{

			String subject = BridgeUtilities.getString(args,""); // The string to strip
			return SleepUtils.getScalar(subject.replaceAll("\\<.*?\\>",""));
		}
	}
	
	// urlEncode(<string>)
	// Encodes a string with standard url-encoding
	private static class UrlEncode implements Function
	{
		public Scalar evaluate(String name, ScriptInstance script, Stack args)
		{
			String subject = BridgeUtilities.getString(args,""); // The string to encode
			String encoding = BridgeUtilities.getString(args,"UTF-8"); // Optional encoding
			if (!subject.equals("")) {
				try {
					return SleepUtils.getScalar(URLEncoder.encode(subject, encoding));
				} catch(Exception e) {
					return SleepUtils.getEmptyScalar();
				}
			}

			// No/invalid arguments
			else {
				return SleepUtils.getEmptyScalar();
			}
		}
	}
	
	// urlDecode(<string>)
	// Decodes a string with standard url-encoding
	private static class UrlDecode implements Function
	{
		public Scalar evaluate(String name, ScriptInstance script, Stack args)
		{
			String subject = BridgeUtilities.getString(args,""); // The string to decode
			String encoding = BridgeUtilities.getString(args,"UTF-8"); // Optional encoding
			if (!subject.equals("")) {
				try {
					return SleepUtils.getScalar(URLDecoder.decode(subject, encoding));
				} catch(Exception e) {
					return SleepUtils.getEmptyScalar();
				}
			}

			// No/invalid arguments
			else {
				return SleepUtils.getEmptyScalar();
			}
		}
	}
}

class HtmlEntityEncoder {
	
	private static HashMap entityTable;

	private final static String[] ENTITYLIST = {
		" ", " ", "-", "-", "'", "'", "`","`",

		"&Uuml;","\u00dc",
		"&Auml;","\u00c4",
		"&Ouml;","\u00d6",
		"&Euml;","\u00cb",
		"&Ccedil;","\u00c7",
		"&AElig;","\u00c6",
		"&Aring;","\u00c5",
		"&Oslash;","\u00d8",

		"&uuml;","\u00fc",
		"&auml;","\u00e4",
		"&ouml;","\u00f6",
		"&euml;","\u00eb",
		"&ccedil;","\u00e7",
		"&aring;","\u00e5",
		"&oslash;","\u00f8",
		"&grave;","`",
		"&agrave;","\u00e0",
		"&egrave;","\u00e8",
		"&igrave;","\u00ec",
		"&ograve;","\u00f2",
		"&ugrave;","\u00f9",
		"&amp;","&",
		"&#34;","\"",

		"&szlig;","\u00df",
		"&nbsp;"," ",
		"&gt;",">",
		"&lt;","<",
		"&copy;","(C)",
		"&cent;","\u00a2",
		"&pound;","\u00a3",
		"&laquo;","\u00ab",
		"&raquo;","\u00bb",
		"&reg;","(R)",
		"&middot;"," - ",
		"&times;"," x ",
		"&acute;","'",
		"&aacute;","\u00e1",
		"&uacute;","\u00fa",
		"&oacute;","\u00f3",
		"&eacute;","\u00e9",
		"&iacute;","\u00ed",
		"&ntilde;","\u00f1",
		"&sect;","\u00a7",
		"&egrave;","\u00e8",
		"&icirc;","\u00ee",
		"&ocirc;","\u00f4",
		"&acirc;","\u00e2",
		"&ucirc;","\u00fb",
		"&ecirc;","\u00ea",
		"&aelig;","\u00e6",
		"&iexcl;","\u00a1",
		"&#151;","-",
		"&#0151;","-",
		"&#0146;","'",
		"&#146;","'",
		"&#0145;","'",
		"&#145;","'",
		"&quot;","\"", };
	
	// Create the initial hashmap
	private static void buildTable() {
		entityTable = new HashMap(ENTITYLIST.length);

		for (int i = 0; i < ENTITYLIST.length; i += 2) {
			if (!entityTable.containsKey(ENTITYLIST[i + 1])) {
				entityTable.put(ENTITYLIST[i + 1], ENTITYLIST[i]);
			}
		}
	}
	
	// Overload
	public static String encode(String subject, String ignore) {
		return encode(subject, 0, subject.length(), ignore);
	}
	
	// Replace special characters with html entities
	public static String encode(String s, int start, int end, String ignore) {
		if (entityTable == null) {
			buildTable(); // Build the table if necessary
		}
	
		StringBuffer sb = new StringBuffer((end - start) * 2);
		char ch;
		
		for (int i = start; i < end; ++i) {
			ch = s.charAt(i);
			
			// Check if the char can be excluded
			if ((ch >= 63 && ch <= 90) || (ch >= 97 && ch <= 122) || ignore.indexOf(ch) != -1) {
				sb.append(ch);
			}
	
			else {
				
				// Encode this char
				sb.append(encodeSingleChar(String.valueOf(ch)));
			}
		}
		
		// Done
		return sb.toString();
	}
	
	// Looks up a single char in the table and returns its equivalent, if any
	protected static String encodeSingleChar(String subject) {
		String replacement = (String)entityTable.get(subject);
		return (replacement == null) ? subject : replacement;
	}
}
