
package sleepy.ssp;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;

import sleepy.ssp.core.*;
import sleepy.ssp.util.*;
import sleepy.bridges.*;

import sleep.interfaces.*;
import sleep.runtime.*;
import sleep.engine.*;
import sleep.error.*;

public class SSPScriptProvider
{
	private SSPScriptCache scriptCache;
	private SSPScriptLoader scriptLoader;

	private LinkedHashSet bridges;
	private LinkedHashSet sspBridges;

	public SSPScriptProvider()
	{
		scriptLoader = new SSPScriptLoader();
		scriptCache = new SSPScriptCache();

		scriptLoader.init( scriptCache );
		scriptCache.init( scriptLoader );
		bridges = new LinkedHashSet();
		sspBridges = new LinkedHashSet();

		addBridge( new Cookies() );
		addBridge( new Sessions() );
	}
	
	public synchronized SSPScript getScriptFor( File root, String pathInContext ) throws IOException
	{
		SSPScript sspScript = scriptCache.getScriptFor( root, pathInContext );
		return sspScript;
	}
	
	public SSPScriptLoader getScriptLoader()
	{
		return scriptLoader;
	}

	public SSPScriptCache getScriptCache()
	{
		return scriptCache;
	}

	public boolean addBridge( Loadable bridge )
	{
		boolean result = bridges.add( bridge );
		if ( result )
		{
			scriptLoader.addBridge( bridge );
			if ( bridge instanceof Protectable )
				scriptLoader.protectFunctions( ((Protectable) bridge).protectedFunctionNames() );
			else
				checkForProtectedFunctions( bridge );
			if ( bridge instanceof SSPLoadable )
				sspBridges.add( bridge );
		}
		return result;
	}

	public void setup( SSPScript sspScript, SSPJettyConnector jettyConnector )
	{
		Iterator iter = sspBridges.iterator();
		while ( iter.hasNext() )
			 ((SSPLoadable) iter.next()).setup( sspScript, jettyConnector );
	}

	public void tearDown( SSPScript sspScript, SSPJettyConnector jettyConnector )
	{
		Iterator iter = sspBridges.iterator();
		while ( iter.hasNext() )
			 ((SSPLoadable) iter.next()).tearDown( sspScript, jettyConnector );
	}

//	public boolean containsBridge( Loadable bridge )
//	{
//		return bridges.contains( bridge );
//	}
//
//	public boolean removeBridge( Loadable bridge )
//	{
//		return bridges.remove( bridge );
//	}

	private void checkForProtectedFunctions( Loadable bridge )
	{
		try
		{
			Field pFuncs = bridge.getClass().getDeclaredField("PROTECTED_FUNCTIONS");
			if ( pFuncs != null )
			{
				if ( !pFuncs.isAccessible() )
					pFuncs.setAccessible(true);
				Object o = pFuncs.get( bridge );
				if ( o instanceof Set )
				{
					Set pfNames = (Set) o;
					scriptLoader.protectFunctions( pfNames );
				}
			}
		}
		catch ( Exception e ) { /* IGNORED */ }
	}
}
