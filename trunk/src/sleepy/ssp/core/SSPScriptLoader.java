
package sleepy.ssp.core;

import sleepy.ssp.util.*;

import sleep.runtime.*;
import sleep.interfaces.*;
import sleep.error.*;
import sleep.parser.*;
import sleep.bridges.*;
import sleep.engine.*;

import java.util.*;
import java.io.*;


/**
 * SSPScriptLoader
 * -------------------------------
 *
 * @author Ralph Becker
 */
public class SSPScriptLoader extends ScriptLoader // implements RuntimeWarningWatcher
{
	//private Logger log = new DefaultLogger("sleepy.ssp.core.SSPScriptLoader");
	private Logger log;
	
	private Hashtable sharedEnv;
	private SSPScriptCache scriptCache;
	private boolean initialized = false;
		
	private final String default_script = "\nprintln('SSPScriptLoader running');\n";

	private ProtectedEnvironment sspEnv;

	public SSPScriptLoader()
	{
		super();
	}
	
	public void init( SSPScriptCache cache )
	{
		if ( !initialized )
		{
			initialized = true;
			scriptCache = cache;
			sharedEnv = new Hashtable();
			sspEnv = new ProtectedEnvironment();
			setEnvironment( sspEnv );
			addGlobalBridge( new SSPBridge( this, scriptCache ) );
			runDefaultScript();
			sspEnv.protectFunctions( sharedEnv.keySet() ); // protect all sleep built-in functions, functions from SSPBridge included
			sspEnv.protectFunctions( SSPConnectorBridge.PROTECTED_FUNCTIONS ); // protect connector bridge functions
		}
	}
	
	public void addBridge( Loadable bridge )
	{
		addGlobalBridge( bridge );
		Hashtable newEnv = new Hashtable();
		try
		{
			loadScript("empty_script", "", newEnv ).run();
		}
		catch ( YourCodeSucksException ycse )
		{
			processScriptErrors( "addBridge("+bridge.toString()+"): ", ycse );
			return;
		}
		sharedEnv = newEnv;
	}
	
	public void addBridges( Loadable[] bridges )
	{
		for ( int i=0; i< bridges.length; i++ )
			addGlobalBridge( bridges[i] );
		Hashtable newEnv = new Hashtable();
		try
		{
			loadScript("empty_script", "", newEnv ).run();
		}
		catch ( YourCodeSucksException ycse )
		{
			processScriptErrors( "addBridges("+bridges.toString()+"): ", ycse );
			return;
		}
		sharedEnv = newEnv;
	}

	public void protectFunction( String function_name )
	{
		sspEnv.protectFunction( function_name );
	}
	
	public void protectFunctions( Set function_names )
	{
		sspEnv.protectFunctions( function_names );
	}
	
	private void setEnvironment( ProtectedEnvironment sspEnv )
	{
		DefaultEnvironment defaultEnv = null;
		Iterator i = bridgesg.iterator();
		while ( i.hasNext() )
		{
			Object n = i.next();
			if ( n instanceof DefaultEnvironment )
				defaultEnv = (DefaultEnvironment) n;
		}
		if ( defaultEnv != null )
		{  // i'm sure it's there
			int index = bridgesg.indexOf( defaultEnv );
			bridgesg.set( index, sspEnv );
		}
	}
	
	private void runDefaultScript()
	{ // run a default script so we have a shareable env
		
		try
		{
			loadScript("default_script", default_script, sharedEnv).run();
		}
		catch ( YourCodeSucksException ycse )
		{
			processScriptErrors( "runDefaultScript("+default_script+"): ", ycse );
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
					log.warn("Warning: " + warning.getMessage() + " at line " + warning.getLineNumber());
				}
			});
			script.run();
			sharedEnv = env;
		}
		catch ( YourCodeSucksException ycse )
		{
			processScriptErrors( "loadEnvironmentScript("+fileName+"): ", ycse );
			return;
		}
		catch ( IOException ioe )
		{
			log.warn( "loadEnvironmentScript("+fileName+"): " + ioe.toString() );
			throw ioe;
		}
		catch ( Exception e )
		{
			log.error( "loadEnvironmentScript("+fileName+"): " + e.toString() );
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
	
//	public static void setLogger( Object logger )
//	{
//		log.setLogger( logger );
//	}

	public void setLogger( Logger logger )
	{
		log = logger;
		log.info( logger.toString() );
	}
	
}
