
package sleepy.ssp.core;

import sleepy.ssp.util.*;

import sleep.interfaces.*;
import sleep.bridges.BridgeUtilities;
import sleep.runtime.*;

import java.io.*;
import java.util.*;

/**
 * SSPConnector bridge for Sleep
 * -------------------------------
 *
 * @author Ralph Becker
 */
public class SSPConnectorBridge implements Loadable, Function
{
	static final HashSet PROTECTED_FUNCTIONS = new HashSet();
	static {
		PROTECTED_FUNCTIONS.add("&getScriptDirectory");
		PROTECTED_FUNCTIONS.add("&getScriptFilename");
		PROTECTED_FUNCTIONS.add("&getScriptName");
		
		PROTECTED_FUNCTIONS.add("&getMethod");
		PROTECTED_FUNCTIONS.add("&getRequestLine");
		PROTECTED_FUNCTIONS.add("&getQuery");
		PROTECTED_FUNCTIONS.add("&getContentType");
		PROTECTED_FUNCTIONS.add("&getContentLength");
		PROTECTED_FUNCTIONS.add("&getCharacterEncoding");
		
		PROTECTED_FUNCTIONS.add("&getRemoteAddress");
		PROTECTED_FUNCTIONS.add("&getRemoteHost");
		PROTECTED_FUNCTIONS.add("&getRemotePort");

		PROTECTED_FUNCTIONS.add("&getServerAddress");
		PROTECTED_FUNCTIONS.add("&getServerName");
		PROTECTED_FUNCTIONS.add("&getServerPort");

		PROTECTED_FUNCTIONS.add("&setStatus");
		PROTECTED_FUNCTIONS.add("&setContentType");
		PROTECTED_FUNCTIONS.add("&setContentLength");
		PROTECTED_FUNCTIONS.add("&setCharacterEncoding");
		PROTECTED_FUNCTIONS.add("&addHeader");
	}

	
	private SSPConnector sspConnector;
	private SSPScript sspScript;
	private File scriptFile;
	
	public SSPConnectorBridge( SSPConnector connector )
	{
		sspConnector = connector;
	}
	
	public static SSPConnectorBridge newInstance( SSPConnector sspConnector )
	{
		return new SSPConnectorBridge( sspConnector );
	}
	
	public boolean scriptLoaded( ScriptInstance script )
	{
		if ( !(script instanceof SSPScript) )
			throw new IllegalArgumentException("SSPConnectorBridge.scriptLoaded: not a SSPScript");
		
		sspScript = (SSPScript) script;
		scriptFile = sspScript.getScriptFile();
		
		Hashtable env = script.getScriptEnvironment().getEnvironment();
		ScriptVariables vars = script.getScriptVariables();
		
		vars.putScalar("%HEADERS", getHeaders() );
		vars.putScalar("%PARAMETERS", getParameters() );
		
		vars.putScalar("$SCRIPT_DIR", getScriptDirectory( true ) );
		vars.putScalar("$SCRIPT_FILENAME", getScriptFilename( true ) );
		vars.putScalar("$SCRIPT_NAME", getScriptName( true ) );
		
		vars.putScalar("$REQUEST_METHOD", getMethod( true ) );
		vars.putScalar("$REQUEST_LINE", getRequestLine( true ) );
		vars.putScalar("$QUERY_STRING", getQuery( true ) );
		vars.putScalar("$CONTENT_TYPE", getContentType( true ) );
		vars.putScalar("$CONTENT_LENGTH", getContentLength( true ) );
		vars.putScalar("$CONTENT_ENCODING", getCharacterEncoding( true ) );
		
		vars.putScalar("$REMOTE_ADDRESS", getRemoteAddress( true ) );
		vars.putScalar("$REMOTE_HOST", getRemoteHost( true ) );
		vars.putScalar("$REMOTE_PORT", getRemotePort( true ) );

		vars.putScalar("$SERVER_ADDRESS", getServerAddress( true ) );
		vars.putScalar("$SERVER_NAME", getServerName( true ) );
		vars.putScalar("$SERVER_PORT", getServerPort( true ) );
		
		//vars.putScalar("", ( true ) );

		env.put("&getScriptDirectory", this );	// the script's parent directory
		env.put("&getScriptFilename", this );	// the script's full path
		env.put("&getScriptName", this ); 		// the script's short name

//		env.put("&getHeaders", this );			// the request headers
//		env.put("&getParameters", this );		// the request parameters (if any)
		
		env.put("&getMethod", this );			// the request method
		env.put("&getRequestLine", this );		// the request's request line
		env.put("&getQuery", this );			// the request's query string (if any)
		env.put("&getContentType", this );		// the request's content-type (if any)
		env.put("&getContentLength", this );	// the request's content-length (if any)
		env.put("&getCharacterEncoding", this );// the request's character encoding (if any)

		env.put("&getRemoteAddress", this );
		env.put("&getRemoteHost", this );
		env.put("&getRemotePort", this );

		env.put("&getServerAddress", this );
		env.put("&getServerName", this );
		env.put("&getServerPort", this );

		//env.put("&", this );
		
		env.put("&setStatus", this );			// set response status
		env.put("&setContentType", this );		// set response content-type, default is text/html
		env.put("&setContentLength", this );	// set response content-length
		env.put("&setCharacterEncoding", this );// set response character encoding
		env.put("&addHeader", this );			// add a field to the response header
		
//		sspConnector.setup( sspScript );
		
		return true;
	}

	public boolean scriptUnloaded( ScriptInstance script )
	{
        if ( !(script instanceof SSPScript) )
            throw new IllegalArgumentException("SSPConnectorBridge.scriptUnloaded: not a SSPScript"); // Not likely, but anyway..

//        SSPScript sspScript = (SSPScript) script;
//
//        sspConnector.tearDown( sspScript );
		return true;
	}

	public Scalar evaluate( String name, ScriptInstance script, Stack args )
	{
		if ( name.equals("&getScriptDirectory") )
		{
			return getScriptDirectory( false );
		}
		else if ( name.equals("&getScriptFilename") )
		{
			return getScriptFilename( false );
		}
		else if ( name.equals("&getScriptName") )
		{
			return getScriptName( false );
		}
		else if ( name.equals("&getMethod") )
		{
			return getMethod( false );
		}
		else if ( name.equals("&getQuery") )
		{
			return getQuery( false );
		}
		else if ( name.equals("&getRequestLine") )
		{
			return getRequestLine( false );
		}
		else if ( name.equals("&getContentType") )
		{
			return getContentType( false );
		}
		else if ( name.equals("&getContentLength") )
		{
			return getContentLength( false );
		}
		else if ( name.equals("&getCharacterEncoding") )
		{
			return getCharacterEncoding( false );
		}
		else if ( name.equals("&getRemoteAddress") )
		{
			return getRemoteAddress( false );
		}
		else if ( name.equals("&getRemoteHost") )
		{
			return getRemoteHost( false );
		}
		else if ( name.equals("&getRemotePort") )
		{
			return getRemotePort( false );
		}
		else if ( name.equals("&getServerAddress") )
		{
			return getServerAddress( false );
		}
		else if ( name.equals("&getServerName") )
		{
			return getServerName( false );
		}
		else if ( name.equals("&getServerPort") )
		{
			return getServerPort( false );
		}
//		else if ( name.equals("&") )
//		{
//			return 
//		}
		else if ( name.equals("&setStatus") )
		{
			int status = BridgeUtilities.getInt( args );
			setStatus( status, BridgeUtilities.getString( args, "" ) );
		}
		else if ( name.equals("&setContentType") )
		{
			String ctype = BridgeUtilities.getString( args, "text/html" );
			setContentType( ctype );
		}
		else if ( name.equals("&setContentLength") )
		{
			int len = BridgeUtilities.getInt( args );
			setContentLength( len );
		}
		else if ( name.equals("&setCharacterEncoding") )
		{
			String encoding = BridgeUtilities.getString( args, "" );
			if ( !"".equals(encoding) )
			{
				setCharacterEncoding( encoding );
			}
		}
		else if ( name.equals("&addHeader") )
		{
			String key = BridgeUtilities.getString( args, "" );
			String value = BridgeUtilities.getString( args, "" );
			if ( !"".equals(key) && !"".equals(value) )
				addHeader( key, value );
		}
		return SleepUtils.getEmptyScalar();
	}
	
	private Scalar getHeaders()
	{
    	//return ReadOnlyScalar.wrap( SSPUtils.getHeadersFromHttpRequest( request ) );
    	return ReadOnlyScalar.wrap( ReadOnlyHash.wrapMap( sspConnector.getHeaders() ) );
	}

	private Scalar getParameters()
	{
    	//return ReadOnlyScalar.wrap( SSPUtils.getHeadersFromHttpRequest( request ) );
    	return ReadOnlyScalar.wrap( ReadOnlyHash.wrapMap( sspConnector.getParameters() ) );
	}
	
	private Scalar getScriptDirectory( boolean readonly )
	{
		if ( readonly )
			return ReadOnlyScalar.wrapString( scriptFile.getParent() );
		else
			return SleepWrapper.wrapString( scriptFile.getParent() );
	}
	
	private Scalar getScriptFilename( boolean readonly )
	{
		if ( readonly )
			return ReadOnlyScalar.wrapString( scriptFile.toString() );
		else
			return SleepWrapper.wrapString( scriptFile.toString() );
	}

	private Scalar getScriptName( boolean readonly )
	{
		if ( readonly )
			return ReadOnlyScalar.wrapString( scriptFile.getName() );
		else
			return SleepWrapper.wrapString( scriptFile.getName() );
	}

	private Scalar getRequestLine( boolean readonly )
	{
		if ( readonly )
			return ReadOnlyScalar.wrapString( sspConnector.getRequestLine() );
		else
			return SleepWrapper.wrapString( sspConnector.getRequestLine() );
	}
	
	private Scalar getQuery( boolean readonly )
	{
		if ( readonly )
			return ReadOnlyScalar.wrap( sspConnector.getQuery() );
		else
			return SleepWrapper.wrap( sspConnector.getQuery() );
	}

	private Scalar getMethod( boolean readonly )
	{
		if ( readonly )
			return ReadOnlyScalar.wrapString( sspConnector.getMethod() );
		else
			return SleepWrapper.wrapString( sspConnector.getMethod() );
	}

	private Scalar getContentType( boolean readonly )
	{
		if ( readonly )
			return ReadOnlyScalar.wrap( sspConnector.getContentType() );
		else
			return SleepWrapper.wrap( sspConnector.getContentType() );
	}
	
	private Scalar getContentLength( boolean readonly )
	{
		if ( readonly )
			return ReadOnlyScalar.wrapInt( sspConnector.getContentLength() );
		else
			return SleepWrapper.wrapInt( sspConnector.getContentLength() );
	}

	private Scalar getCharacterEncoding( boolean readonly )
	{
		if ( readonly )
			return ReadOnlyScalar.wrap( sspConnector.getCharacterEncoding() );
		else
			return SleepWrapper.wrap( sspConnector.getCharacterEncoding() );
	}

	public Scalar getRemoteAddress( boolean readonly )
	{
		if ( readonly )
			return ReadOnlyScalar.wrap( sspConnector.getRemoteAddress() );
		else
			return SleepWrapper.wrap( sspConnector.getRemoteAddress() );
	}

	public Scalar getRemoteHost( boolean readonly )
	{
		if ( readonly )
			return ReadOnlyScalar.wrap( sspConnector.getRemoteHost() );
		else
			return SleepWrapper.wrap( sspConnector.getRemoteHost() );
	}

	public Scalar getRemotePort( boolean readonly )
	{
		if ( readonly )
			return ReadOnlyScalar.wrapInt( sspConnector.getRemotePort() );
		else
			return SleepWrapper.wrapInt( sspConnector.getRemotePort() );
	}

	public Scalar getServerAddress( boolean readonly )
	{
		if ( readonly )
			return ReadOnlyScalar.wrap( sspConnector.getServerAddress() );
		else
			return SleepWrapper.wrap( sspConnector.getServerAddress() );
	}
	
	public Scalar getServerName( boolean readonly )
	{
		if ( readonly )
			return ReadOnlyScalar.wrap( sspConnector.getServerName() );
		else
			return SleepWrapper.wrap( sspConnector.getServerName() );
	}

	public Scalar getServerPort( boolean readonly )
	{
		if ( readonly )
			return ReadOnlyScalar.wrapInt( sspConnector.getServerPort() );
		else
			return SleepWrapper.wrapInt( sspConnector.getServerPort() );
	}
	
	private Scalar getHeaders( boolean readonly )
	{
		if ( readonly )
			return ReadOnlyScalar.wrap( sspConnector.getHeaders() );
		else
			return SleepWrapper.wrap( sspConnector.getHeaders() );
	}

	private Scalar getParameters( boolean readonly )
	{
		if ( readonly )
			return ReadOnlyScalar.wrap( sspConnector.getParameters() );
		else
			return SleepWrapper.wrap( sspConnector.getParameters() );
	}

	private void setStatus( int status, String message )
	{
		if ( message == null || "".equals(message) )
			sspConnector.setStatus( status );
		else 
			sspConnector.setStatus( status, message );
	}

	private void setContentType( String ctype )
	{
		sspConnector.setContentType( ctype );
	}

	private void setContentLength( int len )
	{
		sspConnector.setContentLength( len );
	}

	private void setCharacterEncoding( String encoding )
	{
		sspScript.setEncoding( encoding );
		sspConnector.setCharacterEncoding( encoding );
	}
	
	private void addHeader( String key, String value )
	{
		sspConnector.addHeader( key, value );
	}

	public void destroy()
	{
		sspConnector = null;
		sspScript = null;
		scriptFile = null;
	}

}
