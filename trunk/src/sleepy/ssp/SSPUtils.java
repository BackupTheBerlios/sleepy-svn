
package sleepy.ssp;

import sleepy.ssp.util.*;

import java.util.*;
import org.mortbay.http.*;
import sleep.runtime.*;
import sleep.engine.types.*;

public class SSPUtils
{
//	public static ScalarHash getHeadersFromHttpRequest( HttpRequest httpRequest ) 
//	{
//		HashMap fields = new HashMap();
//		Enumeration enum = httpRequest.getFieldNames();
//		while ( enum.hasMoreElements() ) {
//			String key = enum.nextElement().toString();
//			Enumeration values = httpRequest.getFieldValues( key );
//			fields.put( key, enumToSleepArray( values ) );
//		}
//		return new ReadOnlyHash( fields );
//	}
	
	public static ScalarArray enumToSleepArray( Enumeration enum )
	{
		return new ReadOnlyArray( enumToCollection( enum ) );
	}

//	public static ScalarArray enumToSleepArray( Enumeration enum )
//	{
//		//return new CollectionWrapper( enumToCollection( enum ) );
//		return new ArrayContainer( enumToCollection( enum ) );
//	}
	
	public static Collection enumToCollection( Enumeration enum )
	{
		LinkedList list = new LinkedList();
		
		while ( enum.hasMoreElements() )
			list.add( ReadOnlyScalar.wrap( enum.nextElement() ) );
			//list.add( new ReadOnlyScalar( new ObjectValue(enum.nextElement()) ) );
			//list.add( SleepUtils.getScalar( enum.nextElement() ) );
			//list.add( enum.nextElement() );		
		return list;
	}

//	public static ScalarHash getHeadersFromHttpRequest( HttpRequest httpRequest ) 
//	{
//		HashMap fields = new HashMap();
//		Enumeration enum = httpRequest.getFieldNames();
//		while ( enum.hasMoreElements() ) {
//			String key = enum.nextElement().toString();
//			Enumeration values = httpRequest.getFieldValues( key );
//			//fields.put( key, new CollectionWrapper( enumToCollection( values ) ) );
//			Scalar temp = new Scalar();
//			temp.setValue( enumToSleepArray( values ) );
//			fields.put( key, temp );
//		}
//		return new MapWrapper( fields );
//	}

	public static ScalarHash getHeadersFromHttpRequest( HttpRequest httpRequest ) 
	{
		HashBin fields = new HashBin();
		Enumeration enum = httpRequest.getFieldNames();
		while ( enum.hasMoreElements() ) {
			String key = enum.nextElement().toString();
			Enumeration values = httpRequest.getFieldValues( key );
			//fields.put( key, new CollectionWrapper( enumToCollection( values ) ) );
			//Scalar temp = new ReadOnlyScalar( enumToSleepArray( values ) );
			Scalar temp = ReadOnlyScalar.wrap( enumToSleepArray( values ) );
			//temp.setValue( enumToSleepArray( values ) );
			fields.put( key, temp );
		}
		return fields;
	}
	
}
