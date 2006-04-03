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

        env.put("&htmlEntities", new htmlEntities());
        env.put("&removeHtmlTags", new removeHtmlTags());
        env.put("&urlEncode", new urlEncode());
        env.put("&urlDecode", new urlDecode());
        //env.put("&rawUrlEncode", new rawUrlEncode());
        //env.put("&rawUrlDecode", new rawUrlDecode());

		return true;
	}
	
	public boolean scriptUnloaded(ScriptInstance s) {
		return true;
	}
	
	// htmlEntities(<string>)
	// Converts all special chars to html entities
    private static class htmlEntities implements Function
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
    private static class removeHtmlTags implements Function
    {
        public Scalar evaluate(String name, ScriptInstance script, Stack args)
        {

            String subject = BridgeUtilities.getString(args,""); // The string to strip
            return SleepUtils.getScalar(subject.replaceAll("\\<.*?\\>",""));
        }
    }
    
    // urlEncode(<string>)
	// Encodes a string with standard url-encoding
    private static class urlEncode implements Function
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
    private static class urlDecode implements Function
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

    
    // rawUrlEncode(<string>)
	// Encodes a string with raw url-encoding
    private static class rawUrlEncode implements Function
    {
        public Scalar evaluate(String name, ScriptInstance script, Stack args)
        {
            
            return SleepUtils.getEmptyScalar();
        }
    }
    
    // rawUrlDecode(<string>)
	// Decodes a string with raw url-encoding
    private static class rawUrlDecode implements Function
    {
        public Scalar evaluate(String name, ScriptInstance script, Stack args)
        {
            
            return SleepUtils.getEmptyScalar();
        }
    }

}

class HtmlEntityEncoder {
    private static HashMap entityTable;

    private final static String[] ENTITYLIST = {
        " ", " ", "-", "-", "'", "'", "`","`",

        "&Uuml;","Ü",
        "&Auml;","Ä",
        "&Ouml;","Ö",
        "&Euml;","Ë",
        "&Ccedil;","Ç",
        "&AElig;","Æ",
        "&Aring;","Å",
        "&Oslash;","Ø",

        "&uuml;","ü",
        "&auml;","ä",
        "&ouml;","ö",
        "&euml;","ë",
        "&ccedil;","ç",
        "&aring;","å",
        "&oslash;","ø",
        "&grave;","`",
        "&agrave;","à",
        "&egrave;","è",
        "&igrave;","ì",
        "&ograve;","ò",
        "&ugrave;","ù",
        "&amp;","&",
        "&#34;","\"",

        "&szlig;","ß",
        "&nbsp;"," ",
        "&gt;",">",
        "&lt;","<",
        "&copy;","(C)",
        "&cent;","¢",
        "&pound;","£",
        "&laquo;","«",
        "&raquo;","»",
        "&reg;","(R)",
        "&middot;"," - ",
        "&times;"," x ",
        "&acute;","'",
        "&aacute;","á",
        "&uacute;","ú",
        "&oacute;","ó",
        "&eacute;","é",
        "&iacute;","í",
        "&ntilde;","ñ",
        "&sect;","§",
        "&egrave;","è",
        "&icirc;","î",
        "&ocirc;","ô",
        "&acirc;","â",
        "&ucirc;","û",
        "&ecirc;","ê",
        "&aelig;","æ",
        "&iexcl;","¡",
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