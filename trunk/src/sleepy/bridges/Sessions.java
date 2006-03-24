
package sleepy.bridges;

import sleep.bridges.*;
import sleep.interfaces.*;
import sleep.runtime.*;
import javax.servlet.*;
import javax.servlet.http.*;
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
public class Sessions implements Loadable {

    public boolean scriptLoaded(ScriptInstance s) {
        return true;
    }
    public boolean scriptUnloaded(ScriptInstance s) {
        return true;
    }
    
    // Starts a session
    private static class startSession implements Function {
        public Scalar evaluate(String name, ScriptInstance script, Stack args) {
            return null;
        }
    }
    
    // Stops a session
    private static class stopSession implements Function {
        public Scalar evaluate(String name, ScriptInstance script, Stack args) {
            return null;
        }
    }
    
    // Stops and destroys a session, deleting all session data associated
    // with the given/current session
    private static class destroySession implements Function {
        public Scalar evaluate(String name, ScriptInstance script, Stack args) {
            return null;
        }
    } 
    
    // Returns the current session id
    private static class getSessionID implements Function {
        public Scalar evaluate(String name, ScriptInstance script, Stack args) {
            return null;
        }
    } 
}