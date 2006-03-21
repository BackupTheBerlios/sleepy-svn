
package sleepy.ssp.util;

import sleep.runtime.*;
import sleep.engine.types.*;
import java.util.*;

public class HashBin extends HashContainer
{
	public HashBin()
	{
		super();
	}

	/** initialValues must be a map with Strings as keys and Scalars as values */
	public HashBin( Map initialValues )
	{
		super();
		values.putAll( initialValues );
	}

	public Scalar put(String key, Scalar value)
	{
		return (Scalar) values.put(key, value);
	}

	public Scalar get(String key)
	{
		return (Scalar) values.get(key);
	}
	
	public Scalar getAt(Scalar key)
	{
		Scalar value = (Scalar) values.get(key.getValue().toString());

		if (value == null)
		{
			value = SleepUtils.getEmptyScalar();
			values.put(key.getValue().toString(), value);
		}

		return value;
	}

	public ScalarArray keys()
	{
		ScalarType ntype = SleepUtils.getEmptyScalar().getValue();

		Iterator i = values.values().iterator();
		while (i.hasNext())
		{
			if (((Scalar)i.next()).getValue() == ntype)
				i.remove();
		}

		return new CollectionWrapper(values.keySet());
	}

	public void remove(Scalar value)
	{
		Iterator i = values.keySet().iterator();
		while (i.hasNext())
		{
			if (i.next().toString().equals(value.toString()))
				i.remove();
		}
	}

	public String toString()
	{
		return "(read-only hash " + values.toString() + ")";
	}
	
}
