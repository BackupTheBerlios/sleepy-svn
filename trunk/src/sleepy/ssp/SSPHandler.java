
package sleepy.ssp;

import sleepy.ssp.core.*;

import org.mortbay.util.*;
import org.mortbay.http.*;
import org.mortbay.http.handler.*;

import java.util.*;
import java.io.*;

import org.apache.commons.logging.Log;
import org.mortbay.log.*;

/**
 * SSPHandler
 * -------------------------------
 *
 * @author Ralph Becker
 */
public class SSPHandler implements HttpHandler
{
	private static Log log = LogFactory.getLog(SSPHandler.class);	
	
	private SSPScriptProvider scriptProvider = null;
	
	private File rootDir = null;
	
	private HttpContext context;
	
	private boolean isStarted = false;

	/**
	 * Methods from LifeCycle interface
	 *
	 *
	 */   
	 public boolean isStarted() {
		return this.isStarted;
	 }
		   
	
	public void start() {
		this.isStarted = true;
	}
	
	public void stop() {
		this.isStarted = false;
	}

	/**
	 * Methods from HttpHandler interface
	 *
	 *
	 */				   
	public HttpContext getHttpContext() {
		return this.context;
	}
	
	public String getName() {
		return "Sleep Server Pages handler";
	}
	
	public void handle(java.lang.String pathInContext, 
							java.lang.String pathParams, 
							HttpRequest request, 
							HttpResponse response) {

		if ( rootDir == null && context.getResourceBase() != null )
			setRootDir( context.getResourceBase() );

		if ( pathInContext.toLowerCase().endsWith(".ssp") )
		{
			SSPScript sspScript = null;
			try
			{	
				sspScript = scriptProvider.getScriptFor( rootDir, pathInContext );
				sspScript.setName( pathInContext );
				
				response.setContentType("text/html");
				response.setStatus(response.__200_OK );
				
				sspScript.service( new SSPJettyConnector( request, response, scriptProvider ) );
				
				request.setHandled(true);
			}
			catch ( Exception e )
			{
				//e.printStackTrace();
				log.error( e.toString() );
				throw new RuntimeException( e );
			}
			finally
			{
				if ( sspScript != null )
					sspScript.destroy();
			}

		}
		return;
	}
	
	protected void setRootDir( String resourceBase )
	{
		try
		{
			rootDir = new File( new java.net.URI( resourceBase ) );
		}
		catch ( Exception e )
		{
			log.error(e.toString());
		}
	}
	
	protected File getRootDir()
	{
		return new File( rootDir.toString() );
	}
	
	protected File getScriptFile( String pathInContext )
	{
		return new File( rootDir, pathInContext.substring(1) );
	}
	
	public void initialize(HttpContext context) {
		
		this.context = context;

		Object provider = context.getAttribute(SSPContext.SSP_SCRIPTPROVIDER);
		if ( provider != null )
		{
			scriptProvider = (SSPScriptProvider) provider;
		}
		else 
		{
			scriptProvider = new SSPScriptProvider();
			context.setAttribute(SSPContext.SSP_SCRIPTPROVIDER, scriptProvider);
		}

		if ( context.getResourceBase() != null )
			setRootDir(context.getResourceBase());
		
	} 

}
