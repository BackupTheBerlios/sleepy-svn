
package sleepy.ssp;

import sleepy.ssp.core.*;

import java.io.*;
import java.util.*;

import org.mortbay.http.*;
import org.mortbay.log.*;

import org.apache.commons.logging.Log;

/**
 * SSPHttpContext
 * -------------------------------
 *
 * @author Ralph Becker
 */
public class SSPHttpContext extends HttpContext implements SSPContext
{
	private static Log log = LogFactory.getLog(SSPHttpContext.class);
	
	private LinkedHashSet envScripts = null;
	private SSPScriptProvider scriptProvider = null;
	
    private SSPHandler sspHandler = null;

    /** 
     * Constructor.
     */
    public SSPHttpContext() {
        super();
        envScripts = new LinkedHashSet();
        initSSPContext();
        sspHandler = new SSPHandler();
        addHandler( sspHandler );
		addWelcomeFile("index.ssp");
    }
  
  	public void addEnvironmentScript( String scriptfile )
  	{
		if ( new File( scriptfile ).isFile() )
		{
			if ( !envScripts.contains( scriptfile ) )
			{
  				try
				{
					if ( scriptProvider == null ) initSSPContext();
					scriptProvider.getScriptLoader().loadEnvironmentScript( scriptfile );
				}
				catch ( IOException ioe )
				{ // logged in scriptLoader
	  				throw new RuntimeException( ioe.toString() );
				}
				envScripts.add( scriptfile );
			}
			else
	  		{
	  			log.error(scriptfile  + " already loaded");
	  		}
  		}
  		else
  		{
  			log.error(scriptfile  + " does not exist");
  		}
  	}

	protected void initSSPContext()
	{
		Object provider = getAttribute(SSPContext.SSP_SCRIPTPROVIDER);
		if ( provider != null )
		{
			scriptProvider = (SSPScriptProvider) provider;
		}
		else 
		{
			scriptProvider = new SSPScriptProvider();
			scriptProvider.getScriptLoader().setLogger( SSPUtils.getLogger( scriptProvider.getScriptLoader().getClass() ) );
			setAttribute(SSPContext.SSP_SCRIPTPROVIDER, scriptProvider);
		}
	}

    public String toString()
    {
        return "SSP"+super.toString(); 
    }
}
