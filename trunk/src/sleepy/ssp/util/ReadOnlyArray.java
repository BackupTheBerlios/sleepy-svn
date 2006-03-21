
package sleepy.ssp.util;

import sleep.engine.types.*;
import sleep.runtime.*;
import java.util.*;

public class ReadOnlyArray extends ArrayContainer // implements ScalarArray
{
	protected boolean allowSort = false;

	public ReadOnlyArray()
	{
		super();
	}
	
	/** initialValues must be a collection of Scalars */
	public ReadOnlyArray( Collection initialValues )
	{
		super( initialValues );
	}
	
	/** initialValues must be a collection of Scalars */
	public ReadOnlyArray( Collection initialValues, boolean allowSorting )
	{
		super();
		values.addAll(initialValues);
		allowSort = allowSorting;
	}

	public Scalar add( Scalar value )
	{
		values.add( values.size(), value );
		return value;
	}

	public Scalar get( int i )
	{
		return (Scalar) values.get( i );
	}
	
	public String toString()
	{
		return "(read-only array: " + values.toString() + ")";
	}

	public Scalar pop()
	{
		return SleepUtils.getEmptyScalar();
	}

	public Scalar push(Scalar value)
	{
		return SleepUtils.getEmptyScalar();
	}

	public int size()
	{
		return values.size();
	}

	public void sort(Comparator compare)
	{
		if (allowSort)
			Collections.sort(values, compare);
	}

	public Scalar remove(int index)
	{
		return SleepUtils.getEmptyScalar();
	}

	public Scalar getAt(int index)
	{
      if (index >= size())
      {
          return SleepUtils.getEmptyScalar();
      }

      return (Scalar) values.get(index);
	}

	public Iterator scalarIterator()
	{
		return new ProxyIterator();
	}

	public Scalar add(Scalar value, int index)
	{
		return SleepUtils.getEmptyScalar();
	}

	public void remove(Scalar value)
	{
		// do nothing
	}

	protected class ProxyIterator implements Iterator
	{
		protected Iterator realIterator;

		public ProxyIterator()
		{
			realIterator = values.iterator();
		}

		public boolean hasNext()
		{
			return realIterator.hasNext(); 
		}

		public Object next()
		{
			Object temp = realIterator.next();
 
			if (temp instanceof String)
			{
				return SleepUtils.getScalar((String)temp);
			}

			return SleepUtils.getScalar(temp);
		}

		public void remove()
		{
			 // no dice
		}
	}
	
	protected Stack createValues( Collection initialValues )
	{
		Stack values = new Stack();
		Iterator iter = initialValues.iterator();
		while( iter.hasNext() )
		{
			Object next = iter.next();
			if ( next instanceof Scalar )
				values.add( ReadOnlyScalar.wrapScalar((Scalar) next) );
			else 
				values.add( ReadOnlyScalar.wrapObject(next) );
		}
		return values;
	}
}
