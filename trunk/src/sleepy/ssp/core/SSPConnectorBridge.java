
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
public class SSPConnectorBridge implements Loadable
{
	private SSPConnector sspConnector;
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
		
		SSPScript sspScript = (SSPScript) script;
		scriptFile = sspScript.getScriptFile();
		
		Hashtable env = script.getScriptEnvironment().getEnvironment();
		ScriptVariables vars = script.getScriptVariables();
		
		vars.putScalar("%HEADERS", getHeaders() );
		
		vars.putScalar("$SCRIPT_DIR", getScriptDirectory( true ) );
		vars.putScalar("$SCRIPT_FILENAME", getScriptFilename( true ) );
		vars.putScalar("$SCRIPT_NAME", getScriptName( true ) );
		
		vars.putScalar("$REQUEST_METHOD", getMethod( true ) );
		vars.putScalar("$REQUEST_LINE", getRequestLine( true ) );
		vars.putScalar("$QUERY_STRING", getQuery( true ) );
		vars.putScalar("$CONTENT_TYPE", getContentType( true ) );
		vars.putScalar("$CONTENT_LENGTH", getContentLength( true ) );
		vars.putScalar("$CHARACTER_ENCODING", getCharacterEncoding( true ) );
		//vars.putScalar("", ( true ) );

		env.put("&getScriptDirectory", this );	// the script's parent directory
		env.put("&getScriptFilename", this );	// the script's full path
		env.put("&getScriptName", this ); 		// the script's short name
		
		env.put("&getMethod", this );			// the request method
		env.put("&getRequestLine", this );		// the request's request line
		env.put("&getQuery", this );			// the request's query string (if any)
		env.put("&getContentType", this );		// the request's content-type (if any)
		env.put("&getContentLength", this );	// the request's content-length (if any)
		env.put("&getCharacterEncoding", this );// the request's character encoding (if any)
		//env.put("&", this );
		
		env.put("&setStatus", this );			// set response status
		env.put("&setContentType", this );		// set response content-type, default is text/html
		env.put("&setContentLength", this );	// set response content-length
		env.put("&addHeader", this );			// add a field to the response header
		
		sspConnector.setup( sspScript );
		
		return true;
	}

	public boolean scriptUnloaded( ScriptInstance script )
	{
		return true;
	}

	public Scalar evaluate( String name, ScriptInstance script, Stack args )
	{
		if ( name.equals("&getScriptDirectory") )
		{
			return getScriptDirectory( false );
		}
		else if ( ( name.equals("&getScriptFilename") ) )
		{
			return getScriptFilename( false );
		}
		else if ( ( name.equals("&getScriptName") ) )
		{
			return getScriptName( false );
		}
		else if ( ( name.equals("&getMethod") ) )
		{
			return getMethod( false );
		}
		else if ( ( name.equals("&getQuery") ) )
		{
			return getQuery( false );
		}
		else if ( ( name.equals("&getRequestLine") ) )
		{
			return getRequestLine( false );
		}
		else if ( ( name.equals("&getContentType") ) )
		{
			return getContentType( false );
		}
		else if ( ( name.equals("&getContentLength") ) )
		{
			return getContentLength( false );
		}
		else if ( ( name.equals("&getCharacterEncoding") ) )
		{
			return getCharacterEncoding( false );
		}
//		else if ( ( name.equals("&") ) )
//		{
//			return 
//		}
		else if ( ( name.equals("&setStatus") ) )
		{
			int status = BridgeUtilities.getInt( args );
			setStatus( status, BridgeUtilities.getString( args, "" ) );
		}
		else if ( ( name.equals("&setContentType") ) )
		{
			String ctype = BridgeUtilities.getString( args, "text/html" );
			setContentType( ctype );
		}
		else if ( ( name.equals("&setContentLength") ) )
		{
			int len = BridgeUtilities.getInt( args );
			setContentLength( len );
		}
		else if ( ( name.equals("&setCharacterEncoding") ) )
		{
			String encoding = BridgeUtilities.getString( args, "" );
			if ( !"".equals(encoding) )
			{
				setCharacterEncoding( encoding );
			}
		}
		else if ( ( name.equals("&addHeader") ) )
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
		sspConnector.setCharacterEncoding( encoding );
	}
	
	private void addHeader( String key, String value )
	{
		sspConnector.addHeader( key, value );
	}

}
