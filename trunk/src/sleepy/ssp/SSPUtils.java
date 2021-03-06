
package sleepy.ssp;

import sleepy.ssp.util.*;

import java.util.*;
import org.mortbay.http.*;
import sleep.runtime.*;
import sleep.engine.types.*;

import java.lang.reflect.*;

import org.mortbay.log.*;
import org.apache.commons.logging.Log;

/**
 * SSPUtils
 * -------------------------------
 *
 * @author Ralph Becker
 */
public class SSPUtils
{

	public static Map getHeadersFromHttpRequest( HttpRequest httpRequest )
	{
		HashMap headers = new HashMap();
		Enumeration e = httpRequest.getFieldNames();
		while ( e.hasMoreElements() )
		{
			String key = e.nextElement().toString();
			Enumeration values = httpRequest.getFieldValues( key );
			headers.put( key, CollectionUtils.enumToArray( values ) );
		}
		return headers;
	}
	
	public static Logger getLogger( Class clazz )
	{
		Log log = LogFactory.getLog( clazz );
		return (Logger) Proxy.newProxyInstance( Logger.class.getClassLoader(), new Class[] { Logger.class }, new ProxyLogger( clazz.getName(), log ) );
	}
}
