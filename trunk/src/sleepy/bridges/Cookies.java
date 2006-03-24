
package sleepy.bridges;

import sleep.bridges.*;
import sleep.interfaces.*;
import sleep.runtime.*;
import javax.servlet.*;
import javax.servlet.http.*;
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

    public boolean scriptLoaded(ScriptInstance s) {
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