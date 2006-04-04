
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

import java.io.*;
import java.security.*;

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
            
            // Try to retrieve an existing session
            Object session = sessions.getSession(getSessionIDFromCookie(sspScript));
            if ( session != null && Cookies.getCookie( sspScript, Sessions.sessionID ) != null) {
                sspScript.getScriptVariables().putScalar("%SESSION", ReadOnlyScalar.wrap( (HashBin) session ));
                return SleepUtils.getScalar(getSessionIDFromScript(sspScript));
            }
            else {
            
                // Generate new session id
                String id = Sessions.generateSessionId();
                
                // Set a cookie with the new session id
                Cookies.setCookie( sspScript, Sessions.sessionID, id, 300, "" );
                
                // Create the session data hash
                HashBin sessionHash = new HashBin();
                
                // Add the session id to the hash
                sessionHash.put(Sessions.sessionID , ReadOnlyScalar.wrap(id) );
                
                // Store the hash in the session storage
                sessions.putSession(id, sessionHash);
                
                // Put the hash into the script environment
                script.getScriptVariables().putScalar("%SESSION", ReadOnlyScalar.wrap(sessionHash));
    
                // Return the session id, as a commodity
                return SleepUtils.getScalar(id);
            }
		}
	}
	
	// Stops a session
	private static class stopSession implements Function
	{
		public Scalar evaluate(String name, ScriptInstance script, Stack args)
		{
			if ( !(script instanceof SSPScript) )
				throw new RuntimeException("Sessions.stopSession: not a SSPScript: " + script );
			
            // Delete the cookie
			Cookies.deleteCookie( (SSPScript) script, Sessions.sessionID );
            
            // Remove the %SESSION variable from environment
            script.getScriptVariables().putScalar("%SESSION", SleepUtils.getEmptyScalar());

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
			
            // Remove the session data from storage
            String id = getSessionIDFromScript((SSPScript)script);
            if (id != null) {
    			sessions.removeSession(id);
            }
		
            // Remove the %SESSION variable from environment
            script.getScriptVariables().putScalar("%SESSION", SleepUtils.getEmptyScalar());

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

	private static String getSessionIDFromScript( SSPScript sspScript )
	{
		Scalar sessionScalar = sspScript.getScriptVariables().getScalar("%SESSION");
		if ( sessionScalar != null )
		{
			return ((HashBin)sessionScalar.getHash()).get(Sessions.sessionID).stringValue();
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

	// called before the script runs
	public boolean setup( SSPScript sspScript, SSPConnector sspConnector )
	{
		return true;
	}

	public boolean tearDown( SSPScript sspScript, SSPConnector sspConnector )
	{
       
		Object sessionScalar = sspScript.getScriptVariables().getScalar("%SESSION");
		if ( sessionScalar != null )
		{ 
            // Preserve session hash
			if (Cookies.getCookie( sspScript, Sessions.sessionID ) != null )
                sessions.putSession(getSessionIDFromScript(sspScript), ((Scalar) sessionScalar).objectValue() );
		}

		return true;
	}
    
    private static String generateSessionId() {
        
        MD5Provider md5 = new MD5Provider();
        return md5.MD5(""+System.currentTimeMillis());
    }

}

class MD5Provider {
    
    // The string to digest
    String subject;
    
    public String MD5(String subject) {
        
        this.subject = subject; // The string to be digested
        
        MessageDigest md5; // This is our workhorse
        
        try {
            
            md5  = MessageDigest.getInstance("MD5"); // Get the instance
        }
        catch (Exception e) {
            
            // Error handling here
            return null;
        }
        
        StringBuffer buffer = new StringBuffer(); // This is the buffer for the resulting digest
        
        byte[] digest = md5.digest(this.subject.getBytes()); // perform the md5 digest
        
        String tmp; // Used as a placeholder in the loop
        
        // Loop through the bytes in the digest
        for (int i = 0; i < digest.length; i++) {
            
            tmp = "0" + Integer.toHexString( (0xff & digest[i])); // Create a hex string representing the byte
            buffer.append(tmp.substring(tmp.length()-2)); // Append it to the buffer
        }       
        
        return (buffer.toString()); // Return the result, which should be a 32 byte hex string
    
    }
}