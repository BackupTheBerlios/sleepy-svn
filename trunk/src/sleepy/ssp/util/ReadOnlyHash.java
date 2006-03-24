
package sleepy.ssp.util;

import sleep.runtime.*;
import sleep.engine.types.*;
import java.util.*;

/**
 * ReadOnlyHash
 * -------------------------------
 *
 * @author Ralph Becker
 */
public class ReadOnlyHash extends HashBin
{
	public ReadOnlyHash()
	{
		super();
	}

	/** initialValues must be a map with Strings as keys and Scalars as values */
	public ReadOnlyHash( Map initialValues )
	{
		super( initialValues );
	}

	public Scalar getAt(Scalar key)
	{
		Scalar value = (Scalar) values.get(key.getValue().toString());

		if (value == null)
		{
			  return SleepUtils.getEmptyScalar();
		}

		return value;
	}

	public ScalarArray keys()
	{
//		ScalarType ntype = SleepUtils.getEmptyScalar().getValue();
//
//		Iterator i = values.values().iterator();
//		while (i.hasNext())
//		{
//			if (((Scalar)i.next()).getValue() == ntype)
//				i.remove();
//		}

		return new CollectionWrapper(values.keySet());
	}

	public void remove(Scalar key)
	{
		// do nothing
	}
	
	public String toString()
	{
		return "(read-only hash " + values.toString() + ")";
	}
	
	public static ReadOnlyHash wrapMap( Map map )
	{
		ReadOnlyHash result = new ReadOnlyHash();
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
				if ( obj instanceof ReadOnlyScalar ) // fine
				{
					value = (ReadOnlyScalar) obj;
				}
				else
				{
					value = ReadOnlyScalar.wrap( obj );
				}
			}
			else
			{
				key = next.toString();
				Object obj = map.get( next );
				if ( obj instanceof ReadOnlyScalar ) // fine
				{
					value = (ReadOnlyScalar) obj;
				}
				else
				{
					value = ReadOnlyScalar.wrap( obj );
				}
			}
			result.put( key, value );
		}
		return result;
	}
}
