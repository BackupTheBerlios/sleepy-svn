
package sleepy.ssp;

import sleepy.ssp.core.*;
import sleepy.ssp.util.*;

import sleepy.bridges.*;

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
	private SSPScriptProvider sppScriptProvider;
	
	private MultiPartFormData mpfdata;
	private String plaintext;
	private boolean url_encoded = false;
	private Map parameters;
	
	public SSPJettyConnector( HttpRequest request, HttpResponse response, SSPScriptProvider sppScriptProvider )
	{
		this.request = request;
		this.response = response;
		this.sppScriptProvider = sppScriptProvider;
		
		if ( "POST".equals(getMethod()) )
		{
			try
			{
				readPostData();
			}
			catch ( IOException ioe ) { /* 
				then the request's input stream is invalid and 
				we can asume the response's output stream, too,
				because it's a socket behind it. So we can't 
				even send an error description. */ 
			}
		}
	}

	public HttpRequest getHttpRequest()
	{
		return request;
	}
	
	public HttpResponse getHttpResponse()
	{
		return response;
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

	public String getRemoteAddress()
	{
		return request.getRemoteAddr();
	}

	public String getRemoteHost()
	{
		return request.getRemoteHost();
	}

	public int getRemotePort()
	{
		if ( request.getHttpConnection() != null )
			return request.getHttpConnection().getRemotePort();
		return 0;
	}

	public String getServerAddress()
	{
		if ( request.getHttpConnection() != null )
			return request.getHttpConnection().getServerName();
		return "";
	}
	
	public String getServerName()
	{
		if ( request.getHttpConnection() != null )
			return request.getHttpConnection().getServerName();
		return "";
	}

	public int getServerPort()
	{
		if ( request.getHttpConnection() != null )
			return request.getHttpConnection().getServerPort();
		return request.getPort();
	}		

	public Map getHeaders()
	{
		return SSPUtils.getHeadersFromHttpRequest( request );
	}

	public Map getParameters()
	{
		if ( parameters == null )
		{
			if ( url_encoded || ("GET".equals(getMethod()) && getQuery() != null) )
			{
				parameters = request.getParameterStringArrayMap();
			}
			else if ( mpfdata != null )
			{
				if ( mpfdata.getFileCount() == 0 )
				{
					parameters = new HashMap();
					String[] partnames = mpfdata.getPartNames();
					for ( int i=0; i<partnames.length; i++ )
						parameters.put( partnames[i], mpfdata.getStrings(partnames[i]) );
				}
				else ; // TODO: handle uploaded files
			}
			else if ( plaintext != null )
			{ // TODO: extract key/value pairs
				parameters = new HashMap();
				parameters.put( "PLAINTEXT", new String[] { plaintext } );
			}
			if ( parameters == null )
				parameters = new HashMap();
		}
		return parameters;
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

//	/** 
//	 * Called in SSPConnectorBridge.scriptLoaded 
//	 * after setting up the default environment
//	 */
//    public void setup( SSPScript sspscript )
//    {
//        // add additional setup tasks here
//        Cookies cookies = new Cookies(this.request, this.response);
//        Sessions sessions = new Sessions(this.request, this.response);
//        
//        // Fire the events - I feel this should be done more properly though
//        cookies.scriptLoaded(sspscript);
//        sessions.scriptLoaded(sspscript);
//    }
//
//    /** 
//    * Called in SSPConnectorBridge.scriptUnloaded 
//    */
//    public void tearDown( SSPScript sspscript )
//    {
//        // Tear down stuff here
//    }

	/** 
	 * Called in SSPConnectorBridge.scriptLoaded 
	 * after setting up the default environment
	 */
    public void setup( SSPScript sspscript )
    {
    	// add additional setup tasks here
    	sppScriptProvider.setup( sspscript, this );
    }

    /** 
    * Called in SSPConnectorBridge.scriptUnloaded 
    */
    public void tearDown( SSPScript sspscript )
    {
        // Tear down stuff here
    	sppScriptProvider.tearDown( sspscript, this );
    }
    
	protected void readPostData() throws IOException
	{
		if ( getContentType() == null || getContentLength() <= 0 ) return; // nothing to do
		
		String type = getContentType().toLowerCase();
		
		// "application/x-www-form-urlencoded" is handled by HttpRequest ...
		if (  type.startsWith( "application/x-www-form-urlencoded" ) )
		{ // ... and we force it to do the job now
			request.getParameters();
			url_encoded = true;
		}
		else if ( type.startsWith( "multipart/form-data" ) )
		{
			mpfdata = new MultiPartFormData( request );
		}
		else if ( type.startsWith( "text/plain" ) )
		{ // for now put all into a string
			int length = getContentLength();
			InputStream in = getInputStream();
			byte[] post_data = new byte[length];
			int count = 0;
			int r;
			while ( count < length && (r = in.read()) != -1 )
			{
				post_data[count++] = (byte) (r & 0xFF);
			}
			String encoding = "ISO-8859-1";
			if ( getCharacterEncoding() != null )
				encoding = getCharacterEncoding();
			try
			{
				plaintext = new String( post_data, encoding );
			}
			catch ( UnsupportedEncodingException e )
			{
				plaintext = new String( post_data, "ISO-8859-1" );
			}
		}
	}
}
