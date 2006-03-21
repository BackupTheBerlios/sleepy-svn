
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
	
	public static ScalarArray enumToSleepArray( Enumeration e )
	{
		return new ReadOnlyArray( enumToCollection( e ) );
	}

//	public static ScalarArray enumToSleepArray( Enumeration enum )
//	{
//		//return new CollectionWrapper( enumToCollection( enum ) );
//		return new ArrayContainer( enumToCollection( enum ) );
//	}
	
	public static Collection enumToCollection( Enumeration e )
	{
		LinkedList list = new LinkedList();
		
		while ( e.hasMoreElements() )
			list.add( ReadOnlyScalar.wrap( e.nextElement() ) );
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
		Enumeration e = httpRequest.getFieldNames();
		while ( e.hasMoreElements() ) {
			String key = e.nextElement().toString();
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
