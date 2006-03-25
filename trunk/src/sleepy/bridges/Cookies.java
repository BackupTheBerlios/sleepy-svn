
package sleepy.bridges;

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
public class Cookies implements Loadable {
    private HttpRequest request;
    private HttpResponse response;
    
    public Cookies(HttpRequest request, HttpResponse response) {
        
        this.request = request;
        this.response = response;
    }

    public boolean scriptLoaded(ScriptInstance s) {
        
        // TODO: Possibly add a check here to see that
        // this.setup() has been called.
        return true;
    }
    
    public boolean scriptUnloaded(ScriptInstance s) {
        return true;
    }
    
    // getCookie(<name>)
    // Returns a cookie value
    private static class getCookie implements Function {
        public Scalar evaluate(String name, ScriptInstance script, Stack args) {
            return null;
        }
    }
    
    // setCookie(<name>, <value>, [duration], [domain])
    // Sets a cookie
    private static class setCookie implements Function {
        public Scalar evaluate(String name, ScriptInstance script, Stack args) {
            return null;
        }
    }
    
    // delCookie(<name>)
    // Deletes a cookie entirely
    private static class delCookie implements Function {
        public Scalar evaluate(String name, ScriptInstance script, Stack args) {
            return null;
        }
    }
}