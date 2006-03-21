
package sleepy.ssp.core;

import sleep.bridges.*;
import sleep.interfaces.*;

import sleep.runtime.*;

import java.io.*;
import java.net.*;
import java.util.*;

import sleep.error.*;
import sleep.engine.*;

public class SSPBridge implements Loadable, Function
{
	private static HashMap bridges = new HashMap();
	private SSPScriptLoader scriptLoader = null;
	
	//public SSPBridge() {}
	public SSPBridge( SSPScriptLoader loader )
	{
		scriptLoader = loader;
	}

	public boolean scriptLoaded( ScriptInstance script )
	{
		Hashtable env = script.getScriptEnvironment().getEnvironment();

		env.put( "&output", this );
//
//		ByteSource byteSource = new ByteSource();
//		env.put( "&getBytes", byteSource );
//		env.put( "&loadFile", byteSource );
//
//		GetParameters getParameters = new GetParameters();
//		env.put( "&getParameter", getParameters);
//		env.put( "&getParameters", getParameters);
//		
//		env.put( "&useGlobal", new UseGlobal(scriptLoader));
//		env.put( "&useGlobalClass", new UseGlobalClass(scriptLoader));
//		env.put( "&useGlobalLoadable", new UseGlobalLoadable(scriptLoader));
//
//		env.put( "&include", this);
		
		return true;
	}

	public boolean scriptUnloaded( ScriptInstance script )
	{
		return true;
	}
	
	public Scalar evaluate( String name, ScriptInstance script, Stack args )
	{
		if ( name.equals("&output") && script instanceof SSPScript )
		{
			output( name, (SSPScript) script, args );
		}
		return SleepUtils.getEmptyScalar();
	}

	private void output( String name, SSPScript sspscript, Stack args )
	{
		if ( args.size() == 0 )
		{
			sspscript.fireWarning("output -> no argument", 0);
		}
		else if ( name.equals("&output") )
		{
			//Object argument = BridgeUtilities.getObject( args );
			Object argument = ((Scalar) args.peek()).objectValue();
			
			//System.out.println( "ARGUMENT CLASS: " + argument.getClass().getName() );
			//System.out.println( "ARGUMENT: " + argument );
			
			if ( argument instanceof String )
			{
				String message = BridgeUtilities.getString( args, "" );
				String enc = BridgeUtilities.getString( args, "" );
				if ( !message.equals("") )
				{
					message = message + "\n";
					if ( Boolean.getBoolean("dump.code") )
						System.out.print(message);
					
					try
					{
						if ( enc.length() > 0 )
							sspscript.getOutputStream().write( message.getBytes(enc) ); // use specified charset
						else
							sspscript.getOutputStream().write( message.getBytes() ); // plattform default charset
					}
					catch ( IOException ioe ) // UnsupportedEncodingException extends IOException
					{
						sspscript.fireWarning(ioe.toString(), 0);
					}
				}
			}
			else if ( argument instanceof byte[] )
			{
				byte[] bytes = (byte[]) BridgeUtilities.getObject( args );
				
			//System.out.println( "BYTES: " + new String(bytes) );
			
				if ( bytes.length > 0 )
				{
					try
					{
						sspscript.getOutputStream().write( bytes );
					}
					catch ( IOException ioe )
					{
						sspscript.fireWarning(ioe.toString(), 0);
					}
				}
			}
			else
			{
				sspscript.fireWarning("output -> invalid parameter: " + argument.getClass().getName(), 0);
			}
		}
	}
}