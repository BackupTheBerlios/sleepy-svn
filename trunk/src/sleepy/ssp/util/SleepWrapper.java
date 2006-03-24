
package sleepy.ssp.util;

import sleep.engine.types.*;
import sleep.runtime.*;
import java.util.*;

/**
 * SleepWrapper
 * -------------------------------
 *
 * @author Ralph Becker
 */
public class SleepWrapper
{
	private SleepWrapper() { /* no instance required */ }
		
		
	public static Scalar wrap( Object value )
	{
		if ( value instanceof Scalar )
			return SleepUtils.getScalar( value ); // copy

		if ( value == null ) {
			return SleepUtils.getEmptyScalar();
		}
		else if ( value instanceof Integer ) {
			return wrapInt( (Integer) value );
		}
		else if ( value instanceof Long ) {
			return wrapLong( (Long) value );
		}
		else if ( value instanceof Double ) {
			return wrapDouble( (Double) value );
		}
		else if ( value instanceof String ) {
			return wrapString( (String) value );
		}
		else if ( value instanceof ScalarArray ) {
			return wrapScalarArray( (ScalarArray) value );
		}
		else if ( value instanceof ScalarHash ) {
			return wrapScalarHash( (ScalarHash) value );
		}
		else if ( value instanceof Map ) {
			return wrapMap( (Map) value );
		}
		else if ( value instanceof Collection ) {
			return wrapCollection( (Collection) value );
		}
		else if ( value instanceof Object[] ) {
			return wrapObjectArray( (Object[]) value );
		}
		else {
			return SleepUtils.getScalar( value );
		}
	}
	
	public static Scalar wrapInt( Integer value )
	{
		return wrapInt( value.intValue() );
	}
	
	public static Scalar wrapInt( int value )
	{
		return SleepUtils.getScalar( value );
	}

	public static Scalar wrapLong( Long value )
	{
		return wrapLong( value.longValue() );
	}

	public static Scalar wrapLong( long value )
	{
		return SleepUtils.getScalar( value );
	}

	public static Scalar wrapDouble( Double value )
	{
		return wrapDouble( value.doubleValue() );
	}
	
	public static Scalar wrapDouble( double value )
	{
		return SleepUtils.getScalar( value );
	}

	public static Scalar wrapString( String value )
	{
		return SleepUtils.getScalar( value );
	}

	public static Scalar wrapScalarArray( ScalarArray value )
	{
		return SleepUtils.getScalar( value );
	}
	
	public static Scalar wrapScalarHash( ScalarHash value )
	{
		return SleepUtils.getScalar( value );
	}
	
	public static Scalar wrapObject( Object value )
	{
		return SleepUtils.getScalar( value );
	}
	
	public static Scalar wrapMap( Map value )
	{
		return SleepUtils.getScalar( toScalarHash( value ) );
	}

	public static Scalar wrapCollection( Collection value )
	{
		return SleepUtils.getScalar( toScalarArray( value ) );
	}
	
	public static Scalar wrapObjectArray( Object[] value )
	{
		return SleepUtils.getScalar( toScalarArray( value ) );
	}
		
	public static ScalarHash toScalarHash( Map map )
	{
		HashBin result = new HashBin();
		Iterator keySet = map.keySet().iterator();
		while ( keySet.hasNext() )
		{
			String key;
			Scalar value;
			Object next = keySet.next();
			if ( next instanceof String ) // fine
			{
				key = (String) next;
				Object obj = map.get( next );
				if ( obj instanceof Scalar ) // fine
				{
					value = (Scalar) obj;
				}
				else
				{
					value = wrap( obj );
				}
			}
			else
			{
				key = next.toString();
				Object obj = map.get( next );
				if ( obj instanceof Scalar ) // fine
				{
					value = (Scalar) obj;
				}
				else
				{
					value = wrap( obj );
				}
			}
			result.put( key, value );
		}
		return result;
	}
	
	public static ScalarArray toScalarArray( Collection collection )
	{
		ArrayContainer result = new ArrayContainer();
		Iterator iter = collection.iterator();
		while( iter.hasNext() )
		{
			Object next = iter.next();
			if ( next instanceof Scalar )
				result.add( (Scalar) next, result.size() );
			else 
				result.add( wrap(next), result.size() );
		}
		return result;
	}
	
	public static ScalarArray toScalarArray( Object[] objects )
	{
		ArrayContainer result = new ArrayContainer();
		for( int i=0; i<objects.length; i++ )
		{
			if ( objects[i] instanceof Scalar )
				result.add( (Scalar) objects[i], result.size() );
			else 
				result.add( wrap(objects[i]), result.size() );
		}
		return result;
	}

}