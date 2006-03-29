
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
	private ByteArrayOutputStream out;
	private File scriptFile;

	private SSPConnector sspConnector;
	private String encoding;
	private File rootDir;

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

	OutputStream getOutputStream()
	{
		if ( out == null )
			out = new ByteArrayOutputStream();
		return out;
	}

	void setEncoding( String enc )
	{
		encoding = enc;
	}

	String getEncoding()
	{
		if ( encoding == null )
			return "ISO-8859-1"; // default encoding
		return encoding;
	}
	
	void setScriptFile( File scriptfile )
	{
		scriptFile = scriptfile;
	}
	
	File getScriptFile()
	{
		return scriptFile;
	}

	void setRootDir( File rootdir )
	{
		rootDir = rootdir;
	}

	File getRootDir()
	{
		return new File( rootDir.toString() );
	}

	public void processScriptWarning(ScriptWarning warning)	
	{
	   String message = warning.getMessage();	  
	   int	lineNo  = warning.getLineNumber();
	   String script  = warning.getNameShort(); // name of script 
	   Stack args = new Stack();
	   args.push( SleepUtils.getScalar("ScriptWarning: " + script + "(line " + lineNo + "): " + message + "\n") );
	   callFunction("&writeln", args );
	}

	public SSPConnector getSSPConnector()
	{
		return sspConnector;
	}

	public void service( SSPConnector sspConnector ) throws IOException
	{
		this.sspConnector = sspConnector;
		SSPConnectorBridge bridge = SSPConnectorBridge.newInstance( sspConnector );
		bridge.scriptLoaded( this );
		
		sspConnector.setup( this );
		
		super.run();

		sspConnector.tearDown( this );
		
		sspConnector.setContentLength( out.size() );
		out.writeTo( sspConnector.getOutputStream() );
		
		bridge.destroy();
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
		rootDir = null;
		sspConnector = null;
		encoding = null;
	}

}
