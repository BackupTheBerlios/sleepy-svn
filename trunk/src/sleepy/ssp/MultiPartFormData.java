
package sleepy.ssp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;


import org.apache.commons.logging.Log;
import org.mortbay.log.LogFactory;
import org.mortbay.http.HttpFields;
import org.mortbay.http.HttpRequest;
import org.mortbay.util.LineInput;
import org.mortbay.util.MultiMap;
import org.mortbay.util.StringUtil;

/*
 * Slightly modified class for reading multipart/form-data
 * The original from jetty expects a ServletHttpRequest as
 * constructor parameter which is really not needed.
 */
public class MultiPartFormData
{
	private static Log log = LogFactory.getLog(MultiPartFormData.class);

	/* ------------------------------------------------------------ */
	LineInput _in;
	String _boundary;
	String _encoding;
	byte[] _byteBoundary;
	MultiMap _partMap = new MultiMap(10);
	int _char=-2;
	boolean _lastPart=false;
	int _file_count = 0;
	
	/* ------------------------------------------------------------ */
	/** Constructor. 
	 * @param request The request containing a multipart/form-data
	 * request
	 * @exception IOException IOException
	 */
	public MultiPartFormData(HttpRequest request)
		throws IOException
	{
		//String content_type = request.getHeader(HttpFields.__ContentType);
		String content_type = request.getContentType();
		if (!content_type.startsWith("multipart/form-data"))
			throw new IOException("Not multipart/form-data request");

		if(log.isDebugEnabled())log.debug("Multipart content type = "+content_type);
		_encoding = request.getCharacterEncoding();
		if (_encoding != null)
			_in = new LineInput(request.getInputStream(), 2048, _encoding);
		else
			_in = new LineInput(request.getInputStream());
		
		// Extract boundary string
		_boundary="--"+
			value(content_type.substring(content_type.indexOf("boundary=")));
		
		if(log.isDebugEnabled())log.debug("Boundary="+_boundary);
		_byteBoundary= (_boundary+"--").getBytes(StringUtil.__ISO_8859_1);
		
		loadAllParts();
	}
	
	/* ------------------------------------------------------------ */
	/** Get the part names.
	 * @return an array of part names
	 */
	public String[] getPartNames()
	{
		Set s = _partMap.keySet();
		return (String[]) s.toArray(new String[s.size()]);
	}
	
	/* ------------------------------------------------------------ */
	/** Check if a named part is present 
	 * @param name The part
	 * @return true if it was included 
	 */
	public boolean contains(String name)
	{
		Part part = (Part)_partMap.get(name);
		return (part!=null);
	}
	
	/* ------------------------------------------------------------ */
	/** Get the data of a part as a string.
	 * @param name The part name 
	 * @return The part data
	 */
	public String getString(String name)
	{
		List part = _partMap.getValues(name);
		if (part==null)
			return null;
		if (_encoding != null)
		{
			try 
			{
				return new String(((Part)part.get(0))._data, _encoding);
			} 
			catch (UnsupportedEncodingException uee) 
			{
				if (log.isDebugEnabled())log.debug("Invalid character set: " + uee);
				return null;
			}
		}
		else
			return new String(((Part)part.get(0))._data);
	}
	
	/* ------------------------------------------------------------ */
	/** 
	 * @param name The part name 
	 * @return The parts data
	 */
	public String[] getStrings(String name)
	{
		List parts = _partMap.getValues(name);
		if (parts==null)
			return null;
		String[] strings = new String[parts.size()];
		
		if (_encoding == null) 
		{
			for (int i=0; i<strings.length; i++) 
				strings[i] = new String(((Part)parts.get(i))._data);
		} 
		else 
		{
			try 
			{
				for (int i=0; i<strings.length; i++)
					strings[i] = new String(((Part)parts.get(i))._data, _encoding);
			}
			catch (UnsupportedEncodingException uee) 
			{
				if (log.isDebugEnabled())log.debug("Invalid character set: " + uee);
				return null;
			}
		}
	   
		return strings;
	}
	
	/* ------------------------------------------------------------ */
	/** Get the data of a part as a stream.
	 * @param name The part name 
	 * @return Stream providing the part data
	 */
	public InputStream getInputStream(String name)
	{
		List part = (List)_partMap.getValues(name);
		if (part==null)
			return null;
		return new ByteArrayInputStream(((Part)part.get(0))._data);
	}

	/* ------------------------------------------------------------ */
	public InputStream[] getInputStreams(String name) 
	{
		List parts = (List)_partMap.getValues(name);
		if (parts==null)
			return null;
		InputStream[] streams = new InputStream[parts.size()];
		for (int i=0; i<streams.length; i++) {
			streams[i] = new ByteArrayInputStream(((Part)parts.get(i))._data);
		}
		return streams;
	}

	/* ------------------------------------------------------------ */
	/** Get the MIME parameters associated with a part.
	 * @param name The part name 
	 * @return Hashtable of parameters
	 */
	public Hashtable getParams(String name)
	{
		List part = (List)_partMap.getValues(name);
		if (part==null)
			return null;
		return ((Part)part.get(0))._headers;
	}

	/* ------------------------------------------------------------ */
	public Hashtable[] getMultipleParams(String name) 
	{
		List parts = (List)_partMap.getValues(name);
		if (parts==null)
			return null;
		Hashtable[] params = new Hashtable[parts.size()];
		for (int i=0; i<params.length; i++) {
			params[i] = ((Part)parts.get(i))._headers;
		}
		return params;
	}


	/* ------------------------------------------------------------ */
	/** Get number of files in multipart request.
	 * @return total number of files
	 */
	public int getFileCount()
	{ // by rabe
		return _file_count;
	}

	/* ------------------------------------------------------------ */
	/** Get number of files in multipart request.
	 * @param name The part name 
	 * @return number of files for param name
	 */
	public int getFileCount( String name )
	{ // by rabe
		int count = 0;
		String[] filenames = getFilenames( name );
		if ( filenames == null ) return count;
		for (int i=0; i<filenames.length; i++) {
			if ( filenames != null )
				++count;
		}
		return count;
	}
	
	/* ------------------------------------------------------------ */
	/** Get any file name associated with a part.
	 * @param name The part name 
	 * @return The filename
	 */
	public String getFilename(String name)
	{
		List part = (List)_partMap.getValues(name);
		if (part==null)
			return null;
		return ((Part)part.get(0))._filename;
	}

	/* ------------------------------------------------------------ */
	public String[] getFilenames(String name) 
	{
		List parts = (List)_partMap.getValues(name);
		if (parts==null)
			return null;
		String[] filenames = new String[parts.size()];
		for (int i=0; i<filenames.length; i++) {
			filenames[i] = ((Part)parts.get(i))._filename;
		}
		return filenames;
	}

	/* ------------------------------------------------------------ */
	private void loadAllParts()
		throws IOException
	{
		// Get first boundary
		String line = _in.readLine();
		if (!line.equals(_boundary))
		{
			log.warn(line);
			throw new IOException("Missing initial multi part boundary");
		}
		
		// Read each part
		while (!_lastPart)
		{
			// Read Part headers
			Part part = new Part();
			
			String content_disposition=null;
			while ((line=_in.readLine())!=null)
			{
				// If blank line, end of part headers
				if (line.length()==0)
					break;

				if(log.isDebugEnabled())log.debug("LINE="+line);
				
				// place part header key and value in map
				int c = line.indexOf(':',0);
				if (c>0)
				{
					String key = line.substring(0,c).trim().toLowerCase();
					String value = line.substring(c+1,line.length()).trim();
					String ev = (String) part._headers.get(key);
					part._headers.put(key,(ev!=null)?(ev+';'+value):value);
					if(log.isDebugEnabled())log.debug(key+": "+value);
					if (key.equals("content-disposition"))
						content_disposition=value;
				}
			}

			// Extract content-disposition
			boolean form_data=false;
			if (content_disposition==null)
			{
				throw new IOException("Missing content-disposition");
			}
			
			StringTokenizer tok =
				new StringTokenizer(content_disposition,";");
			while (tok.hasMoreTokens())
			{
				String t = tok.nextToken().trim();
				String tl = t.toLowerCase();
				if (t.startsWith("form-data"))
					form_data=true;
				else if (tl.startsWith("name="))
					part._name=value(t);
				else if (tl.startsWith("filename="))
				{
					++_file_count;
					part._filename=value(t);
				}
			}

			// Check disposition
			if (!form_data)
			{
				log.warn("Non form-data part in multipart/form-data");
				continue;
			}
			if (part._name==null || part._name.length()==0)
			{
				log.warn("Part with no name in multipart/form-data");
				continue;
			}
			if(log.isDebugEnabled())log.debug("name="+part._name);
			if(log.isDebugEnabled())log.debug("filename="+part._filename);
			_partMap.add(part._name,part);
			part._data=readBytes();
		}	   
	}

	/* ------------------------------------------------------------ */
	private byte[] readBytes()
		throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		int c;
		boolean cr=false;
		boolean lf=false;
		
		// loop for all lines`
		while (true)
		{
			int b=0;
			while ((c=(_char!=-2)?_char:_in.read())!=-1)
			{
				_char=-2;

				// look for CR and/or LF
				if (c==13 || c==10)
				{
					if (c==13) _char=_in.read();
					break;
				}

				// look for boundary
				if (b>=0 && b<_byteBoundary.length && c==_byteBoundary[b])
					b++;
				else
				{
					// this is not a boundary
					if (cr) baos.write(13);
					if (lf) baos.write(10);
					cr=lf=false;
					
					if (b>0)
						baos.write(_byteBoundary,0,b);
					b=-1;
				  
					baos.write(c);
				}
			}

			// check partial boundary
			if ((b>0 && b<_byteBoundary.length-2) ||
				(b==_byteBoundary.length-1))
			{
				if (cr) baos.write(13);
				if (lf) baos.write(10);
				cr=lf=false;
				baos.write(_byteBoundary,0,b);
				b=-1;
			}
			
			// boundary match
			if (b>0 || c==-1)
			{
				if (b==_byteBoundary.length)
					_lastPart=true;
				if (_char==10) _char=-2;
				break;
			}
			
			// handle CR LF
			if (cr) baos.write(13);
			if (lf) baos.write(10);
			cr=(c==13);
			lf=(c==10 || _char==10);
			if (_char==10) _char=-2;  
		}
		if(log.isTraceEnabled())log.trace(baos.toString());
		return baos.toByteArray();
	}
	
	
	/* ------------------------------------------------------------ */
	private String value(String nameEqualsValue)
	{   
		String value =
			nameEqualsValue.substring(nameEqualsValue.indexOf('=')+1).trim();
		
		int i=value.indexOf(';');
		if (i>0)
			value=value.substring(0,i);
		if (value.startsWith("\""))
		{
			value=value.substring(1,value.indexOf('"',1));
		}
		
		else
		{
			i=value.indexOf(' ');
			if (i>0)
				value=value.substring(0,i);
		}
		return value;
	}
	
	/* ------------------------------------------------------------ */
	private class Part
	{
		String _name=null;
		String _filename=null;
		Hashtable _headers= new Hashtable(10);
		byte[] _data=null;
	}	
};
