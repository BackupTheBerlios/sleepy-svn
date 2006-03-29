
package sleepy.ssp.core;

import sleepy.ssp.util.*;

import java.util.*;
import java.io.*;

import sleep.runtime.*;
import sleep.engine.*;
import sleep.error.*;

/**
 * SSPScriptCache
 * -------------------------------
 *
 * @author Ralph Becker
 */
public class SSPScriptCache
{
	private boolean storeBytes = false;
	private HashMap blockCache;
	private HashMap lastModifiedCache;
	
	private boolean initialized = false;
	private SSPScriptLoader scriptLoader;

	public SSPScriptCache() {}
	
	/**
	 * Creates a cache for storing Blocks.
	 * if store_bytes is set to true a serialized 
	 * Block is stored as a byte array and a fresh
	 * copy is returned. Default is store blocks.
	 */
	public SSPScriptCache(  boolean store_bytes )
	{
		storeBytes = store_bytes;
	}

	public void init( SSPScriptLoader loader )
	{
		if ( !initialized )
		{
			initialized = true;
			blockCache = new HashMap();
			lastModifiedCache = new HashMap();
			scriptLoader = loader;
		}
	}
	
	public synchronized SSPScript getScriptFor( File root, String pathInContext ) throws IOException
	{
		 SSPScript sspScript = getScriptFor( new File( root, pathInContext.substring(1) ) );
		 sspScript.setRootDir( root );
		 sspScript.setName( pathInContext );
		 return sspScript;
	}
	
	protected SSPScript getScriptFor( File sppScriptFile ) throws IOException
	{
		SSPScript result = null;
		Block block = null;
		long refresh = needsRefresh( sppScriptFile );
		if ( refresh > 0L )
		{
			Object refreshed = refresh( sppScriptFile );
			if ( refreshed instanceof YourCodeSucksException ) 
			{
				result = new SSPErrorScript( (YourCodeSucksException) refreshed, scriptLoader.copySharedEnv() );
				result.setScriptFile( sppScriptFile );
				return result;
			}
			else
			{
				block = (Block) refreshed;
			}
		}
		else 
		{
			if ( refresh == 0L )
			{ // file not found or io error
				result = new SSPErrorScript( "File not found: ", scriptLoader.copySharedEnv() );
				result.setScriptFile( sppScriptFile );
				return result;
			}
			else 
			{
				block = getBlock( sppScriptFile );
			}
		}
		
		result = new SSPScript( block, scriptLoader.copySharedEnv() );
		result.setScriptFile( sppScriptFile );
		
		return result;
	}

	public synchronized Block getBlockFor( File parent, String filename ) throws IOException
	{
		Block block = null;
		File includeFile = new File( parent, filename );
		long refresh = needsRefresh( includeFile );
		if ( refresh > 0L )
		{
			Object refreshed = refresh( includeFile );
			if ( refreshed instanceof YourCodeSucksException ) 
			{
				return BlockUtils.errorsToBlock( (YourCodeSucksException) refreshed );
			}
			else
			{
				block = (Block) refreshed;
			}
		}
		else 
		{
			if ( refresh == 0L )
			{ // file not found or io error
				return block;
			}
			else 
			{
				block = getBlock( includeFile );
			}
		}
		return block;
	}	
	
	/**
	 * refreshs block if sppScriptFile has been changed 
	 */
	protected Object refresh( File sspScriptFile ) throws IOException
	{
		String code = FileUtils.readFile( sspScriptFile );
		
if ( Boolean.getBoolean("dump.code") )
	System.out.println( "file: " + code );

		if ( FileUtils.hasExtension(sspScriptFile, ".ssp" ) )
			code = SSPParser.getCode( code + "\n\n" );
			
if ( Boolean.getBoolean("dump.code") )
	System.out.println( "code: " + code );

		Object result = BlockUtils.compile( code );
		if ( result instanceof Block )
			putBlock( sspScriptFile, (Block) result );
		return result;
	}
	
	/**
	 * returns a Block from cache
	 *
	 *
	 */
	protected Block getBlock( File sppScriptFile )
	{
		Object cacheHit = blockCache.get( sppScriptFile.toString() );
		// if ( cacheHit == null ) 
 		//	throw new RuntimeException("SSPScriptCache.getBlock: no block found for: " + sppScriptFile.toString());
		if ( storeBytes )
			return BlockUtils.byteArrayToBlock( (byte[]) cacheHit );
		else 
			return (Block) cacheHit;
	}
	
	/**
	 * Stores a Block into cache
	 *
	 *
	 */
	protected void putBlock( File sppScriptFile, Block block )
	{
		if ( storeBytes )
			blockCache.put( sppScriptFile.toString(), BlockUtils.blockToByteArray( block ) );
		else
			blockCache.put( sppScriptFile.toString(), block );
		lastModifiedCache.put( sppScriptFile.toString(), lastModified( sppScriptFile ) );
	}
	
	/**
	 * Checks if sppScriptFile has been changed 
	 *
	 *
	 */
	protected long needsRefresh( File sppScriptFile )
	{
		Object cacheHit = lastModifiedCache.get( sppScriptFile.toString() );
		if ( cacheHit != null ) {
			long lastModified = sppScriptFile.lastModified();
			if ( !( lastModified > ((Long)cacheHit).longValue()) ) {
				return -1L; /* indicates no refresh needed.
				This is necessary since file.lastModified() can return 0L 
				if the file does not exists or an IO error occures */ 
			}
			return lastModified;
		}
   		return sppScriptFile.lastModified();
	}
	
	protected Long lastModified( File file )
	{
		return new Long( file.lastModified() );
	}
}
