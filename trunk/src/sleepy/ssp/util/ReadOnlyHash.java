
package sleepy.ssp.util;

import sleep.runtime.*;
import sleep.engine.types.*;
import java.util.*;

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
	
}
