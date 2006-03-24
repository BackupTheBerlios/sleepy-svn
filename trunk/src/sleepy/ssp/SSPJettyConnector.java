
package sleepy.ssp;

import sleepy.ssp.core.*;
import sleepy.ssp.util.*;

import sleep.runtime.*;

import java.io.*;
import java.util.*;

import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;

/**
 * SSPJettyConnector
 * -------------------------------
 *
 * @author Ralph Becker
 */
public class SSPJettyConnector implements SSPConnector
{
	private HttpRequest request;
	private HttpResponse response;
	
	public SSPJettyConnector( HttpRequest request, HttpResponse response )
	{
		this.request = request;
		this.response = response;
	}

	//----- request stuff
	
	public InputStream getInputStream()
	{
		return request.getInputStream();
	}

	public String getRequestLine()
	{
		return request.getRequestLine();
	}

	public String getMethod()
	{
		return request.getMethod();
	}
	
	public String getQuery()
	{
		return request.getQuery();
	}

	public String getContentType()
	{
		return request.getContentType();
	}
	
	public int getContentLength()
	{
		return request.getContentLength();
	}

	public String getCharacterEncoding()
	{
		return request.getCharacterEncoding();
	}

	public Map getHeaders()
	{
		return SSPUtils.getHeadersFromHttpRequest( request );
	}

	public Map getParameters()
	{
		// TODO: 
		// - evaluate query string
		// - read/evaluate post data
		return new HashMap();
	}

	//----- response stuff

	public OutputStream getOutputStream()
	{
		return response.getOutputStream();
	}
	
	public void setStatus( int status )
	{
		response.setStatus( status );
	}
	
	public void setStatus( int status, String message )
	{
		response.setStatus( status, message );
	}
	
	public void setContentType( String ctype )
	{
		response.setContentType( ctype );
	}
	
	public void setContentLength( int len )
	{
		response.setContentLength( len );
	}
	
	public void setCharacterEncoding( String encoding )
	{
		response.setCharacterEncoding( encoding, true );
	}

	public void addHeader( String key, String value )
	{
		response.setField( key, value );
	}

	/** 
	 * Called in SSPConnectorBridge.scriptLoaded 
	 * after setting up the default environment
	 */
	public void setup( SSPScript sspscript )
	{
		// add additional setup tasks here
		
	}

}
