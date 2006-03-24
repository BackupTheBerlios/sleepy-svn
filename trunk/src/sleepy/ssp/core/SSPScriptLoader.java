
package sleepy.ssp.core;

import sleep.runtime.*	;
import sleep.interfaces.*;
import sleep.error.*;
import sleep.parser.*;
import sleep.bridges.*;
import sleep.engine.*;

import java.util.*;
import java.io.*;

import org.apache.commons.logging.Log;

/**
 * SSPScriptLoader
 * -------------------------------
 *
 * @author Ralph Becker
 */
public class SSPScriptLoader extends ScriptLoader // implements RuntimeWarningWatcher
{
	private static Log log; // = org.mortbay.log.LogFactory.getLog(SSPScriptLoader.class);
	
	static {
		try {
			Class lfc = Class.forName( System.getProperty("sleepy.ssp.logfactory", "org.mortbay.log.LogFactory") );
			if ( lfc != null ) {
				java.lang.reflect.Method glm = lfc.getDeclaredMethod( "getLog", new Class[] { Class.class } );
				if ( glm != null )
					log = (Log) glm.invoke( null, new Object[] { SSPScriptLoader.class } );
			}
		}
		catch ( Exception e ) { System.err.println(e.toString()); }
	}
	
	private Hashtable sharedEnv;

	private final String default_script = "\nprintln('SSPScriptLoader running');\n";

	public SSPScriptLoader()
	{
		super();
		sharedEnv = new Hashtable();
		addGlobalBridge( new SSPBridge(this) );
		runDefaultScript();
	}
	
	private void runDefaultScript()
	{ // run a default script so we have a shareable env
		
		try
		{
			loadScript("default_script", default_script, sharedEnv).run();
		}
		catch ( YourCodeSucksException ycse )
		{
			processScriptErrors( "SSPScriptLoader.runDefaultScript("+default_script+"): ", ycse );
			return;
		}
	}

	public void loadEnvironmentScript( String fileName ) throws IOException
	{
		try
		{
			Hashtable env = copySharedEnv();
			ScriptInstance script = loadScript( fileName, env );
			script.addWarningWatcher(
			new RuntimeWarningWatcher() {
				public void processScriptWarning(ScriptWarning warning)
				{
					log.error("Warning: " + warning.getMessage() + " at line " + warning.getLineNumber());
				}
			});
			script.run();
			sharedEnv = env;
		}
		catch ( YourCodeSucksException ycse )
		{
			processScriptErrors( "SSPScriptLoader.loadEnvironmentScript("+fileName+"): ", ycse );
			return;
		}
		catch ( IOException ioe )
		{
			log.warn( "SSPScriptLoader.loadEnvironmentScript("+fileName+"): " + ioe.toString() );
			throw ioe;
		}
		catch ( Exception e )
		{
			log.error( "SSPScriptLoader.loadEnvironmentScript("+fileName+"): " + e.toString() );
			throw new RuntimeException( e.toString() );
		}
	}
	
	Hashtable copySharedEnv()
	{
		return new Hashtable( sharedEnv );
	}

	private void processScriptErrors(String message,YourCodeSucksException ex)
	{
		LinkedList errors = ex.getErrors();
		Iterator i = errors.iterator();
		log.error( message );
		while (i.hasNext())
		{
			SyntaxError anError = (SyntaxError)i.next();
			StringBuffer buffer = new StringBuffer("Error: ");
			buffer.append(anError.getDescription()).append(" at line ").append(anError.getLineNumber());
			buffer.append("\n");
			buffer.append("      ").append(anError.getCodeSnippet());
			buffer.append("\n");
			if (anError.getMarker() != null)
				buffer.append("      " + anError.getMarker());
			log.error( buffer.toString() );
		}
	}
	
//	public void processScriptWarning(ScriptWarning warning)
//	{
//		log.error("Warning: " + warning.getMessage() + " at line " + warning.getLineNumber());
//	}
}
