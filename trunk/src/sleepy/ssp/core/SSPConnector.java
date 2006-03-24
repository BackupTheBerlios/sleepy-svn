
package sleepy.ssp.core;

import java.io.*;
import java.util.*;

/**
 * SSPConnector
 * -------------------------------
 *
 * @author Ralph Becker
 */
public interface SSPConnector
{
	/** 
	 * Called in SSPConnectorBridge.scriptLoaded
	 */
	public void setup( SSPScript sspscript );
	
	//----- request stuff ----- 
	
	/** The request input stream */
	public InputStream getInputStream();

	/** The request line */
	public String getRequestLine();

	/** The request method */
	public String getMethod();

	/** The request query string */
	public String getQuery();

	/** The request content type, if any */
	public String getContentType();
	
	/** The request content length */
	public int getContentLength();

	/** The request character encoding, if any */
	public String getCharacterEncoding();

	/** The request header data
	 * A SSPConnector is responsible to provide them.
	 * Format: String key -> String[] values
	 */
	public Map getHeaders();

	/** The request query parameters, if any.
	 * A SSPConnector is responsible to provide them
	 * Format: String key -> String[] values
	 */
	public Map getParameters();

	//----- response stuff ----- 

	/** The response output stream */
	public OutputStream getOutputStream();

	/** The response status */
	public void setStatus( int status );
	public void setStatus( int status, String message );

	/** The content type of the response, default is text/html */
	public void setContentType( String ctype );
	
	/** The content length of the response */
	public void setContentLength( int len );

	/** The character encoding of the response */
	public void setCharacterEncoding( String encoding );

	/** Add a response header field */
	public void addHeader( String key, String value );
	//public void addHeader( String key, String[] value );
	
}
