
package sleepy.bridges;

import sleepy.ssp.core.SSPLoadable;
import sleepy.ssp.core.SSPScript;
import sleepy.ssp.core.SSPConnector;
import sleepy.ssp.SSPJettyConnector;

import sleepy.ssp.util.*;

import sleep.bridges.*;
import sleep.interfaces.*;
import sleep.runtime.*;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import java.util.*;

import javax.servlet.http.Cookie;
	
/**
 * Cookie bridge for Sleep
 * -------------------------------
 * This is a generic cookie handling bridge for Sleep
 * Designed to interface with Sleepy
 *
 * @author Andreas Ravnestad
 * @author Ralph Becker
 * @since 1.0
 */
public class Cookies implements SSPLoadable /*, Protectable*/ {
	
	static HashSet PROTECTED_FUNCTIONS = new HashSet();
	static {
		PROTECTED_FUNCTIONS.add("&getCookie");
		PROTECTED_FUNCTIONS.add("&setCookie");
		PROTECTED_FUNCTIONS.add("&delCookie");
	}
	
	/* if implements Protectable
	public Set protectedFunctionNames()
	{
		return PROTECTED_FUNCTIONS;
	}
	*/
	
	public Cookies() {
		
	}

	public boolean scriptLoaded(ScriptInstance s) {
		
		Hashtable env = s.getScriptEnvironment().getEnvironment();

		env.put("&getCookie", new getCookie() );
		env.put("&setCookie", new setCookie() );
		env.put("&delCookie", new delCookie() );

		return true;
	}
	
	public boolean scriptUnloaded(ScriptInstance s) {
		return true;
	}
	
	// getCookie(<name>)
	// Returns a cookie value
	private static class getCookie implements Function
	{
		public Scalar evaluate(String name, ScriptInstance script, Stack args)
		{
			if ( !(script instanceof SSPScript) )
				throw new RuntimeException("Cookies.getCookie: not a SSPScript: " + script );
			
			String cookieName = BridgeUtilities.getString(args,"");
			
			if ( !cookieName.equals("") )
			{
				Cookie cookie = getCookie( (SSPScript) script, cookieName );
				if ( cookie != null )
				{
					HashBin hash = new HashBin();
					hash.put("name", SleepWrapper.wrap( cookie.getName() ) );
					hash.put("value", SleepWrapper.wrap( cookie.getValue() ) );
					return SleepWrapper.wrap( hash );	
				}
			}
			return SleepUtils.getEmptyScalar();
		}
	}
	
	// setCookie(<name>, <value>, [duration], [domain])
	// Sets a cookie
	private static class setCookie implements Function
	{
		public Scalar evaluate(String name, ScriptInstance script, Stack args)
		{
			if ( !(script instanceof SSPScript) )
				throw new RuntimeException("Cookies.setCookie: not a SSPScript: " + script );

			String cookieName = BridgeUtilities.getString(args,"");
			String cookieValue = BridgeUtilities.getString(args,"");
			int cookieDuration = BridgeUtilities.getInt(args,-1);
			String cookieDomain = BridgeUtilities.getString(args,"");
			
			return SleepUtils.getScalar( setCookie( (SSPScript) script, cookieName, cookieValue, cookieDuration, cookieDomain ) );
		}
	}
	
	// delCookie(<name>)
	// Deletes a cookie entirely
	private static class delCookie implements Function
	{
		public Scalar evaluate(String name, ScriptInstance script, Stack args)
		{
			if ( !(script instanceof SSPScript) )
				throw new RuntimeException("Cookies.delCookie: not a SSPScript: " + script );

			String cookieName = BridgeUtilities.getString(args,"");
			
			return SleepUtils.getScalar( deleteCookie( (SSPScript) script, cookieName ) );
		}
	}
	
	private static HttpRequest getHttpRequest( SSPScript sspScript )
	{
		return ((SSPJettyConnector) sspScript.getSSPConnector()).getHttpRequest();
	}
	
	private static HttpResponse getHttpResponse( SSPScript sspScript )
	{
		return ((SSPJettyConnector) sspScript.getSSPConnector()).getHttpResponse();
	}

	static Cookie[] getCookies( SSPScript sspScript )
	{
		return getHttpRequest( sspScript ).getCookies();
	}

	static Cookie getCookie( SSPScript sspScript, String cookieName )
	{
		Cookie[] cookies = getCookies( sspScript );
		Cookie cookie = null;
		for ( int i=0; i<cookies.length; i++ )
		{
			if ( cookieName.equals(cookies[i].getName()) )
			{
				cookie = cookies[i];
				break;
			}
		}
		return cookie;
	}
	
	static boolean setCookie( SSPScript sspScript, String name, String value, int duration, String domain )
	{
		if ( !name.equals("") && !value.equals("") )
		{
			Cookie cookie = new Cookie( name, value );
			if ( duration > 0 )
				cookie.setMaxAge( duration );
			if ( !"".equals(domain) )
				cookie.setDomain( domain );
			return setCookie( sspScript, cookie ); // cookie set
		}
		return false; // cookie not set
	}

	static boolean setCookie( SSPScript sspScript, Cookie cookie )
	{
		getHttpResponse( sspScript ).addSetCookie( cookie );
		return true; // cookie set
	}
	
	static boolean deleteCookie( SSPScript sspScript, String cookieName )
	{
		if ( !cookieName.equals("") )
		{
			Cookie cookie = getCookie( sspScript, cookieName );
			if ( cookie != null) // cookie exists
			{
				cookie.setMaxAge( 0 );
				return setCookie( sspScript, cookie );
			}
		}
		return false;
	}
	
	public boolean setup( SSPScript sspScript, SSPConnector sspConnector )
	{
		// SSPJettyConnector jettyConnector = (SSPJettyConnector) sspConnector;
		// HttpRequest httpRequest = jettyConnector.getHttpRequest();
		// HttpResponse httpResponse = jettyConnector.getHttpResponse();
		// ...
		
		//System.out.println( "Cookies.setup(" + sspScript.toString() + ", " + sspConnector.toString() +  " ): " + sspScript.getName() );
		return true;
	}

	public boolean tearDown( SSPScript sspScript, SSPConnector sspConnector )
	{
		// SSPJettyConnector jettyConnector = (SSPJettyConnector) sspConnector;
		// HttpRequest httpRequest = jettyConnector.getHttpRequest();
		// HttpResponse httpResponse = jettyConnector.getHttpResponse();
		// ...

		//System.out.println( "Cookies.tearDown(" + sspScript.toString() +", " + sspConnector.toString() +  " ): " + sspScript.getName() );
		return true;
	}

}