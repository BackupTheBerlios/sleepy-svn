
package sleepy.ssp.util;

import sleep.runtime.*	;
import sleep.interfaces.*;
import sleep.error.*;
import sleep.parser.*;
import sleep.bridges.*;
import sleep.engine.*;

import java.io.*;
import java.util.*;

/**
 * BlockUtils
 * -------------------------------
 *
 * @author Ralph Becker
 */
public class BlockUtils
{
	private BlockUtils() { /* no instance needed */ }
	
	public static byte[] blockToByteArray( final Block block )
	{
		byte[] blockByteArray = null;
		if ( block == null ) throw new IllegalArgumentException("blockToByteArray: block is null");
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject( block );
			blockByteArray = baos.toByteArray();
		}
		catch ( Exception e ) // IOException or ClassNotFoundException
		{ // should not happen (except with an OutOfMemoryException)
			throw new RuntimeException(e.toString());
		}
		return blockByteArray;
	}

	public static Block byteArrayToBlock( final byte[] blockByteArray )
	{
		Block block = null;
		if ( blockByteArray == null ) return block;
		try
		{
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(blockByteArray));
			block = (Block) ois.readObject();
		}
		catch ( Exception e ) // IOException or ClassNotFoundException
		{ // should not happen (except with an OutOfMemoryException)
			throw new RuntimeException(e.toString());
		}
		return block;
	} 
	
	public static Block clone( final Block block )
	{
		Block clone = null;
		if ( block == null ) return clone;
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject( block );
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
			clone = (Block) ois.readObject();
		}
		catch ( Exception e ) // IOException or ClassNotFoundException
		{ // should not happen (except with an OutOfMemoryException)
			throw new RuntimeException(e.toString());
		}
		return clone;
	} 

//	private static Object LOCK = new Object();
	/**
	 * Returns the runnable Block or the YourCodeSucksException
	 */
	public static Object compile( String code )
	{
		Object result = null;
//		synchronized (LOCK)
//		{
			try
			{
				result = SleepUtils.ParseCode(code.toString());
			}
			catch (YourCodeSucksException ycs)
			{
			 	result = ycs;
			}
//		}
		return result;
	}

	public static Block errorsToBlock( YourCodeSucksException ycse )
	{
		Iterator i = ycse.getErrors().iterator();
		StringBuffer buffer = new StringBuffer("");
		buffer.append("writeln('YourCodeSucksException for: ' . $pathInContext);\n");
		while (i.hasNext())
		{
			SyntaxError error = (SyntaxError)i.next();
			buffer.append("writeln(' line ").append(error.getLineNumber()).append(": ").append( error.getDescription()).append(" ');\n");
		}
		Object result = compile( buffer.toString() );
		if ( result instanceof Block )
			return (Block) result;
		return (Block) null;
	}

}
