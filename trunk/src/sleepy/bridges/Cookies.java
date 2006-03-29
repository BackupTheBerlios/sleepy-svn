
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
 * Cookie bridge for Sleep
 * -------------------------------
 * This is a generic cookie handling bridge for Sleep
 * Designed to interface with Sleepy
 *
 * @author Andreas Ravnestad
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
    private static class getCookie implements Function {
        public Scalar evaluate(String name, ScriptInstance script, Stack args) {
            return SleepUtils.getEmptyScalar();
        }
    }
    
    // setCookie(<name>, <value>, [duration], [domain])
    // Sets a cookie
    private static class setCookie implements Function {
        public Scalar evaluate(String name, ScriptInstance script, Stack args) {
            return SleepUtils.getEmptyScalar();
        }
    }
    
    // delCookie(<name>)
    // Deletes a cookie entirely
    private static class delCookie implements Function {
        public Scalar evaluate(String name, ScriptInstance script, Stack args) {
            return SleepUtils.getEmptyScalar();
        }
    }
    
    public boolean setup( SSPScript sspScript, SSPConnector sspConnector )
    {
    	// SSPJettyConnector jettyConnector = (SSPJettyConnector) sspConnector;
    	// HttpRequest httpRequest = jettyConnector.getHttpRequest();
    	// HttpResponse httpResponse = jettyConnector.getHttpResponse();
    	// ...
    	
    	System.out.println( "Cookies.setup(" + sspScript.toString() + ", " + sspConnector.toString() +  " ): " + sspScript.getName() );
    	return true;
    }

    public boolean tearDown( SSPScript sspScript, SSPConnector sspConnector )
    {
    	// SSPJettyConnector jettyConnector = (SSPJettyConnector) sspConnector;
    	// HttpRequest httpRequest = jettyConnector.getHttpRequest();
    	// HttpResponse httpResponse = jettyConnector.getHttpResponse();
    	// ...

    	System.out.println( "Cookies.tearDown(" + sspScript.toString() +", " + sspConnector.toString() +  " ): " + sspScript.getName() );
    	return true;
    }

}