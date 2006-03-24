
package sleepy.ssp.core;

import sleepy.ssp.util.*;

import sleep.runtime.*;
import sleep.engine.*;
import sleep.error.*;

import java.io.*;
import java.util.*;

/**
 * SSPScript
 * -------------------------------
 *
 * @author Ralph Becker
 */
public class SSPScript extends ScriptInstance implements RuntimeWarningWatcher
{
	private OutputStream out;
	private File scriptFile;

	SSPScript( Hashtable env )
	{
		super( env );
	}

	public SSPScript( Block block, Hashtable env )
	{
		super( env );
		installBlock( block );
		addWarningWatcher( this );
	}

	void setOutputStream( OutputStream out_stream )
	{
		out = out_stream;
	}

	OutputStream getOutputStream()
	{
		return out;
	}
	
	void setScriptFile( File scriptfile )
	{
		scriptFile = scriptfile;
	}
	
	File getScriptFile()
	{
		return scriptFile;
	}

	public void processScriptWarning(ScriptWarning warning)	
	{
	   String message = warning.getMessage();	  
	   int	lineNo  = warning.getLineNumber();
	   String script  = warning.getNameShort(); // name of script 
	   Stack args = new Stack();
	   args.push( SleepUtils.getScalar("ScriptWarning: " + script + "(line " + lineNo + "): " + message + "\n") );
	   callFunction("&output", args );
	}

	public void service( SSPConnector sspConnector )
	{
		setOutputStream( sspConnector.getOutputStream() );
		
		SSPConnectorBridge.newInstance( sspConnector ).scriptLoaded( this );
		
		super.run();
		
	}

	public void destroy()
	{
		out = null;
		name = null;
		loaded = false; 
		watchers = null; 
		environment = null;
		variables = null;
		script = null;
		scriptFile = null;
	}

}
