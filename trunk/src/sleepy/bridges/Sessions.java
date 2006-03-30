
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
 * Session bridge for Sleep
 * -------------------------------
 * This is a bridge for manipulating HTTP sessions for Sleep
 * Designed to interface with Sleepy
 * Requires the Cookie handling bridge to be loaded
 *
 * @author Andreas Ravnestad
 * @author Ralph Becker
 * @since 1.0
 */
public class Sessions implements SSPLoadable /*, Protectable*/ {
	
	static HashSet PROTECTED_FUNCTIONS = new HashSet();
	static {
		PROTECTED_FUNCTIONS.add("&startSession");
		PROTECTED_FUNCTIONS.add("&stopSession");
		PROTECTED_FUNCTIONS.add("&destroySession");
		PROTECTED_FUNCTIONS.add("&getSessionID");
	}
	
	/* if implements Protectable
	public Set protectedFunctionNames()
	{
		return PROTECTED_FUNCTIONS;
	}
	*/
	
	public static final String sessionID = "session-id";	
	private static SessionStorage sessions = new VolatileSessionStorage();
	
	public Sessions() {
	}

	public boolean scriptLoaded(ScriptInstance s) {
		
		Hashtable env = s.getScriptEnvironment().getEnvironment();

		env.put("&startSession", new startSession() );
		env.put("&stopSession", new stopSession() );
		env.put("&destroySession", new destroySession() );
		env.put("&getSessionID", new getSessionID() );

		return true;
	}
	
	public boolean scriptUnloaded(ScriptInstance s) {
		return true;
	}
	
	// Starts a session
	private static class startSession implements Function
	{
		public Scalar evaluate(String name, ScriptInstance script, Stack args)
		{
			if ( !(script instanceof SSPScript) )
				throw new RuntimeException("Sessions.startSession: not a SSPScript: " + script );
			
			SSPScript sspScript = (SSPScript) script;
			String hostaddress = getHostAddress( sspScript );
			String id = ""+System.currentTimeMillis();
			Cookies.setCookie( sspScript, Sessions.sessionID, id, 300, "" );
			HashBin sessionHash = new HashBin();
			sessionHash.put(Sessions.sessionID , ReadOnlyScalar.wrap(id) );
			sessionHash.put("session-data" , ReadOnlyScalar.wrap(new HashBin()) );
			sessions.putSession( hostaddress, sessionHash );
			script.getScriptVariables().putScalar("%SESSION", ReadOnlyScalar.wrap(sessionHash));

			return SleepUtils.getEmptyScalar();
		}
	}
	
	// Stops a session
	private static class stopSession implements Function
	{
		public Scalar evaluate(String name, ScriptInstance script, Stack args)
		{
			if ( !(script instanceof SSPScript) )
				throw new RuntimeException("Sessions.stopSession: not a SSPScript: " + script );
			
			Cookies.deleteCookie( (SSPScript) script, Sessions.sessionID );
			
			return SleepUtils.getEmptyScalar();
		}
	}
	
	// Stops and destroys a session, deleting all session data associated
	// with the given/current session
	private static class destroySession implements Function
	{
		public Scalar evaluate(String name, ScriptInstance script, Stack args)
		{
			if ( !(script instanceof SSPScript) )
				throw new RuntimeException("Sessions.destroySession: not a SSPScript: " + script );
			
			sessions.removeSession( getHostAddress( (SSPScript) script ) );
		
			return SleepUtils.getEmptyScalar();
		}
	} 
	
	// Returns the current session id
	private static class getSessionID implements Function
	{
		public Scalar evaluate(String name, ScriptInstance script, Stack args)
		{
			if ( !(script instanceof SSPScript) )
				throw new RuntimeException("Sessions.getSessionID: not a SSPScript: " + script );
			
			String id = getSessionIDFromScript( (SSPScript) script );
			if ( id != null )
				return SleepUtils.getScalar( id );
			
			return SleepUtils.getEmptyScalar();
		}
	} 

//	private static void startSession()
//	{
//			Object session = sessions.getSession( hostaddress );
//			
//			if ( session == null )
//			{
//				String id = ""+System.currentTimeMillis();
//				Cookies.setCookie( sspScript, sessionID, id, 300, "" );
//				HashBin sessionHash = new HashBin();
//				sessionHash.put(Sessions.sessionID , ReadOnlyScalar.wrap(id) );
//				sessionHash.put("session-data" , ReadOnlyScalar.wrap(new HashBin()) );
//				sessions.putSession( hostaddress, sessionHash );
//				script.getScriptVariables().putScalar("%SESSION", ReadOnlyScalar.wrap(sessionHash));
//			}
//			else // we have a session
//			{ 	 // then we must have a cookie or the session is expired
//				Cookie cookie = Cookies.getCookie( sspScript, sessionID );
//				if ( cookie != null )
//				{
//					HashBin sessionHash = (HashBin) session;
//					String id = sessionHash.get(Sessions.sessionID).stringValue();
//					if ( cookie.getValue().equals(id) )
//					{ // allright, 
//						
//					}
//				}
//				else
//				{
//					HashBin sessionHash = (HashBin) sessions.remove( hostaddress );
//				}
//				script.getScriptVariables().putScalar("%SESSION", ReadOnlyScalar.wrap( session ));
//			}
//	}

	private static String getSessionIDFromScript( SSPScript sspScript )
	{
		Scalar sessionScalar = sspScript.getScriptVariables().getScalar("%SESSION");
		if ( sessionScalar != null )
		{
			return ((HashBin) sessionScalar.getHash()).get(Sessions.sessionID).stringValue();
		}
		return null;
	}

	private static String getSessionIDFromCookie( SSPScript sspScript )
	{
		Cookie cookie = Cookies.getCookie( sspScript, Sessions.sessionID );
		if ( cookie != null )
		{
			return cookie.getValue();
		}
		return null;
	}

	private static HttpRequest getHttpRequest( SSPScript sspScript )
	{
		return ((SSPJettyConnector) sspScript.getSSPConnector()).getHttpRequest();
	}
	
	private static HttpResponse getHttpResponse( SSPScript sspScript )
	{
		return ((SSPJettyConnector) sspScript.getSSPConnector()).getHttpResponse();
	}

	private static String getHostAddress( SSPScript sspScript )
	{
		return sspScript.getSSPConnector().getRemoteAddress();
	}
	
	// called before the script runs
	public boolean setup( SSPScript sspScript, SSPConnector sspConnector )
	{
		// SSPJettyConnector jettyConnector = (SSPJettyConnector) sspConnector;
		// HttpRequest httpRequest = jettyConnector.getHttpRequest();
		// HttpResponse httpResponse = jettyConnector.getHttpResponse();
		// ...
		
		String hostaddress = sspConnector.getRemoteAddress();
		Object session = sessions.getSession( hostaddress );
		if ( session != null )
		{ // restore session hash
			if ( Cookies.getCookie( sspScript, Sessions.sessionID ) != null)
				sspScript.getScriptVariables().putScalar("%SESSION", ReadOnlyScalar.wrap( (HashBin) session ));
			// else sessions.removeSession( hostaddress );
		}
		
		//System.out.println( "Sessions.setup(" + sspScript.toString() +", " + sspConnector.toString() +  " ): " + sspScript.getName() );
		return true;
	}

	// called when the script 
	public boolean tearDown( SSPScript sspScript, SSPConnector sspConnector )
	{
		// SSPJettyConnector jettyConnector = (SSPJettyConnector) sspConnector;
		// HttpRequest httpRequest = jettyConnector.getHttpRequest();
		// HttpResponse httpResponse = jettyConnector.getHttpResponse();
		// ...

		String hostaddress = sspConnector.getRemoteAddress();
		Object sessionScalar = sspScript.getScriptVariables().getScalar("%SESSION");
		if ( sessionScalar != null )
		{ // preserve session hash
			if ( Cookies.getCookie( sspScript, Sessions.sessionID ) != null )
				sessions.putSession( hostaddress, ((Scalar) sessionScalar).objectValue() );
			// else sessions.removeSession( hostaddress );

		}

		//System.out.println( "Sessions.tearDown(" + sspScript.toString() +", " + sspConnector.toString() +  " ): " + sspScript.getName() );
		return true;
	}

}