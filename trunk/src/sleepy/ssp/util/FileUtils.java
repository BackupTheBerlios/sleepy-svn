
package sleepy.ssp.util;

import java.io.*;

import java.nio.*;
import java.nio.charset.*;

public class FileUtils
{
	private FileUtils() { /* no instance needed */ }
	
	public static String readFile( File file ) throws IOException
	{
		return readFile( file, false );
	}
	
	public static String readFile( File file, boolean disableConversions ) throws IOException
	{
		return readStream( new FileInputStream( file ) , disableConversions );
	}

	public static String readStream( InputStream in ) throws IOException
	{
		return readStream( in, false );
	}
	
	public static String readStream( InputStream in, boolean disableConversions ) throws IOException
	{
		StringBuffer code = new StringBuffer("");
		IOException exception = null;
		BufferedReader reader = new BufferedReader(getInputStreamReader(in, disableConversions));
		try
		{
			reader = new BufferedReader(getInputStreamReader(in, disableConversions));
			String s = reader.readLine();
			while (s != null)
			{
				code.append("\n");
				code.append(s);
				s = reader.readLine();
			}
		}
		catch ( IOException ioe )
		{
			exception = ioe;
		}
		finally
		{
			if ( reader != null )
				reader.close();
			in.close();
		}
		if ( exception != null )
			throw exception;
		return code.toString();
	}
	
	private static InputStreamReader getInputStreamReader( InputStream in, boolean disableConversions )
	{
		if (disableConversions)
		{
			return new InputStreamReader(in, new NoConversion());
		}

		return new InputStreamReader(in);
	}
	
	private static class NoConversion extends CharsetDecoder
	{
		public NoConversion() 
		{
	 		super(null, 1.0f, 1.0f);
		}

		protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out)
		{
			int mark = in.position();
			try
			{
				while (in.hasRemaining())
				{
					if (!out.hasRemaining())
						return CoderResult.OVERFLOW;

					int index = (int)in.get();
					if (index >= 0)
					{
						out.put((char)index);
					}
					else
					{
						index = 256 + index;
						out.put((char)index);
					}
					mark++;
				}
				return CoderResult.UNDERFLOW;
			}
			finally { in.position(mark); }
		}
	 }
}
