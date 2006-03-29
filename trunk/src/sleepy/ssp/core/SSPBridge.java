
package sleepy.ssp.core;

import sleepy.ssp.util.*;

import sleep.bridges.*;
import sleep.interfaces.*;

import sleep.runtime.*;

import java.io.*;
import java.net.*;
import java.util.*;

import sleep.error.*;
import sleep.engine.*;

/**
 * SSPBridge
 * -------------------------------
 *
 * @author Ralph Becker
 */
public class SSPBridge implements Loadable, Function
{
	private static HashMap bridges = new HashMap();
	private SSPScriptLoader scriptLoader = null;
	private SSPScriptCache scriptCache = null;
	
	//public SSPBridge() {}
	public SSPBridge( SSPScriptLoader loader, SSPScriptCache cache )
	{
		scriptLoader = loader;
		scriptCache = cache;
	}

	public boolean scriptLoaded( ScriptInstance script )
	{
		Hashtable env = script.getScriptEnvironment().getEnvironment();

		env.put( "&write", this );
		env.put( "&writeln", this );

		env.put( "&include", this );

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
		if ( name.equals("&write") && script instanceof SSPScript )
		{
			output( name, (SSPScript) script, args, false );
		}
		else if ( name.equals("&writeln") && script instanceof SSPScript )
		{
			output( name, (SSPScript) script, args, true );
		}
		else if ( name.equals("&include") && script instanceof SSPScript )
		{
			include( name, (SSPScript) script, args );
		}
		return SleepUtils.getEmptyScalar();
	}

	private void output( String name, SSPScript sspscript, Stack args, boolean ln )
	{
		if ( args.size() == 0 )
		{
			sspscript.fireWarning(name + " -> no argument", 0);
		}
		else if ( name.equals("&write") || name.equals("&writeln") )
		{
			//Object argument = BridgeUtilities.getObject( args );
			Object argument = ((Scalar) args.peek()).objectValue();
			
			//System.out.println( "ARGUMENT CLASS: " + argument.getClass().getName() );
			//System.out.println( "ARGUMENT: " + argument );
			
			if ( argument instanceof String )
			{
				String message = BridgeUtilities.getString( args, "" );
				//String enc = BridgeUtilities.getString( args, sspscript.getEncoding() );
				String enc = sspscript.getEncoding(); // default is ISO-8859-1
				if ( !message.equals("") )
				{
					if ( ln )
						message = message + "\r\n";
					
					//if ( Boolean.getBoolean("dump.code") )
					//	System.out.print(message);
					
					try
					{
						if ( enc.length() > 0 )
							sspscript.getOutputStream().write( message.getBytes(enc) ); // use specified charset
						else
							sspscript.getOutputStream().write( message.getBytes() );
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
				sspscript.fireWarning(name + " -> invalid parameter: " + argument.getClass().getName(), 0);
			}
		}
	}
	
	private void include( String name, SSPScript sspscript, Stack args )
	{
		String filename = BridgeUtilities.getString( args, "" );
		
		try
		{
			if ( FileUtils.hasExtension(filename,".ssp") || FileUtils.hasExtension(filename,".sli") )
			{
				Block code = null;
				if ( filename.startsWith("/") )
				{
					code = scriptCache.getBlockFor( sspscript.getRootDir(), filename );
				}
				else
				{
					code = scriptCache.getBlockFor( sspscript.getScriptFile().getParentFile(),filename );
				}
				
				if ( code != null )
				{
					SleepUtils.runCode( sspscript, code );
				}
				else 
				{
					args.clear();
					args.push( "include("+ filename +"): file not found" );
					sspscript.callFunction("&writeln", args );
				}
			}
			else
			{
				String text = "";
				if ( filename.startsWith("/") )
				{
					text = FileUtils.readFile( new File( sspscript.getRootDir(), filename ) );
				}
				else
				{
					text = FileUtils.readFile( new File(sspscript.getScriptFile().getParentFile(),filename) );
				}
				args.clear();
				args.push( SleepUtils.getScalar( text ) );
				sspscript.callFunction("&writeln", args );
			}
		}
		catch ( IOException ioe )
		{
			sspscript.fireWarning( "include(" + filename + "): " + ioe.toString() , 0);
		}
	}

}
