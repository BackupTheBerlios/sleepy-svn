
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
	private SSPScriptLoader scriptLoader = null;
	private SSPScriptCache scriptCache = null;
	
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
					if ( scriptLoader == null ) initSSPContext();
					scriptLoader.loadEnvironmentScript( scriptfile );
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
		Object loader = getAttribute(SSPContext.SSP_SCRIPTLOADER);
		if ( loader != null )
		{
			scriptLoader = (SSPScriptLoader) loader;
		}
		else 
		{
			scriptLoader = new SSPScriptLoader();
			setAttribute(SSPContext.SSP_SCRIPTLOADER, scriptLoader);
		}
		Object cache = getAttribute(SSPContext.SSP_SCRIPTCACHE);
		if ( cache != null )
		{
			scriptCache = (SSPScriptCache) cache;
		}
		else 
		{
			scriptCache = new SSPScriptCache(scriptLoader);
			setAttribute(SSPContext.SSP_SCRIPTCACHE, scriptCache);
		}
	}

    public String toString()
    {
        return "SSP"+super.toString(); 
    }
}
