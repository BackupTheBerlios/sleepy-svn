
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
	
	private SSPScriptLoader scriptLoader;
	
	public SSPScriptCache( SSPScriptLoader loader )
	{
		blockCache = new HashMap();
		lastModifiedCache = new HashMap();
		scriptLoader = loader;
	}
	/**
	 * Creates a cache for storing Blocks.
	 * if store_bytes is set to true a serialized 
	 * Block is stored as a byte array and a fresh
	 * copy is returned. Default is store blocks.
	 */
	public SSPScriptCache(  SSPScriptLoader loader, boolean store_bytes )
	{
		blockCache = new HashMap();
		lastModifiedCache = new HashMap();
		scriptLoader = loader;
		storeBytes = store_bytes;
	}
	
	public synchronized SSPScript getScriptFor( File sppScriptFile ) throws IOException
	{
		SSPScript result = null;
		Block block = null;
		long refresh = needsRefresh( sppScriptFile );
		if ( refresh > 0L )
		{
			Object refreshed = refresh( sppScriptFile );
			if ( refreshed instanceof YourCodeSucksException ) 
			{
				return new SSPErrorScript( (YourCodeSucksException) refreshed, sppScriptFile, scriptLoader.copySharedEnv() );
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
				return new SSPErrorScript( "File not found: ", sppScriptFile, scriptLoader.copySharedEnv() );
			}
			else 
			{
				block = getBlock( sppScriptFile );
			}
		}
		
		result = new SSPScript( block, scriptLoader.copySharedEnv() );
		result.setName( sppScriptFile.toString() );
		result.setScriptFile( sppScriptFile );
		
		return result;
	}
	
	/**
	 * refreshs block if sppScriptFile has been changed 
	 */
	protected Object refresh( File sppScriptFile ) throws IOException
	{
		String code = FileUtils.readFile( sppScriptFile );
	//System.out.println( "file: " + code );
		code = SSPParser.getCode( code + "\n\n" );
	//System.out.println( "code: " + code );
		Object result = BlockUtils.compile( code );
		if ( result instanceof Block )
			putBlock( sppScriptFile, (Block) result );
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
