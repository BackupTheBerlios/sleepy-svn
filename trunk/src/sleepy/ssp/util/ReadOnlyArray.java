
package sleepy.ssp.util;

import sleep.engine.types.*;
import sleep.runtime.*;
import java.util.*;

/**
 * ReadOnlyArray
 * -------------------------------
 *
 * @author Ralph Becker
 */
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
	
	public static ReadOnlyArray wrapCollection( Collection collection )
	{
		ReadOnlyArray result = new ReadOnlyArray();
		Iterator iter = collection.iterator();
		while( iter.hasNext() )
		{
			Object next = iter.next();
			if ( next instanceof ReadOnlyScalar )
				result.add( (ReadOnlyScalar) next );
			else 
				result.add( ReadOnlyScalar.wrap(next) );
		}
		return result;
	}
	
	public static ReadOnlyArray wrapArray( Object[] objects )
	{
		ReadOnlyArray result = new ReadOnlyArray();
		for( int i=0; i<objects.length; i++ )
		{
			if ( objects[i] instanceof ReadOnlyScalar )
				result.add( (ReadOnlyScalar) objects[i] );
			else 
				result.add( ReadOnlyScalar.wrap(objects[i]) );
		}
		return result;
	}

}
