
package sleepy.bridges;

import sleepy.ssp.core.SSPLoadable;
import sleepy.ssp.core.SSPScript;
import sleepy.ssp.core.SSPConnector;
import sleepy.ssp.SSPJettyConnector;

import sleep.bridges.*;
import sleep.interfaces.*;
import sleep.runtime.*;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import java.util.*;

/**
 * Session bridge for Sleep
 * -------------------------------
 * This is a bridge for manipulating HTTP sessions for Sleep
 * Designed to interface with Sleepy
 * Requires the Cookie handling bridge to be loaded
 *
 * @author Andreas Ravnestad
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
    private static class startSession implements Function {
        public Scalar evaluate(String name, ScriptInstance script, Stack args) {
            return SleepUtils.getEmptyScalar();
        }
    }
    
    // Stops a session
    private static class stopSession implements Function {
        public Scalar evaluate(String name, ScriptInstance script, Stack args) {
            return SleepUtils.getEmptyScalar();
        }
    }
    
    // Stops and destroys a session, deleting all session data associated
    // with the given/current session
    private static class destroySession implements Function {
        public Scalar evaluate(String name, ScriptInstance script, Stack args) {
            return SleepUtils.getEmptyScalar();
        }
    } 
    
    // Returns the current session id
    private static class getSessionID implements Function {
        public Scalar evaluate(String name, ScriptInstance script, Stack args) {
            return SleepUtils.getEmptyScalar();
        }
    } 
    
    // called before the script runs
    public boolean setup( SSPScript sspScript, SSPConnector sspConnector )
    {
    	// SSPJettyConnector jettyConnector = (SSPJettyConnector) sspConnector;
    	// HttpRequest httpRequest = jettyConnector.getHttpRequest();
    	// HttpResponse httpResponse = jettyConnector.getHttpResponse();
    	// ...
    	
		System.out.println( "Sessions.setup(" + sspScript.toString() +", " + sspConnector.toString() +  " ): " + sspScript.getName() );
		return true;
    }

    // called when the script 
    public boolean tearDown( SSPScript sspScript, SSPConnector sspConnector )
    {
    	// SSPJettyConnector jettyConnector = (SSPJettyConnector) sspConnector;
    	// HttpRequest httpRequest = jettyConnector.getHttpRequest();
    	// HttpResponse httpResponse = jettyConnector.getHttpResponse();
    	// ...

		System.out.println( "Sessions.tearDown(" + sspScript.toString() +", " + sspConnector.toString() +  " ): " + sspScript.getName() );
		return true;
	}

}