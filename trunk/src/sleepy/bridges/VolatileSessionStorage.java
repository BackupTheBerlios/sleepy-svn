
package sleepy.bridges;

import java.util.*;

public class VolatileSessionStorage implements SessionStorage
{
	private HashMap storage;
	private Object lock;

	public VolatileSessionStorage()
	{
		lock = new Object();
		storage = new HashMap();
	}

	public Object putSession( Object key, Object data )
	{
		Object result = null;
		synchronized ( lock ) 
		{
			result = storage.put( key, data );
		}
		return result;
	}
	
	public Object getSession( Object key )
	{
		Object result = null;
		synchronized ( lock ) 
		{
			result = storage.get( key );
		}
		return result;
	}
	
	public Object removeSession( Object key )
	{
		Object result = null;
		synchronized ( lock ) 
		{
			result = storage.remove( key );
		}
		return result;
	}

	public boolean hasSession( Object key )
	{
		boolean result = false;
		synchronized ( lock ) 
		{
			result = storage.containsKey( key );
		}
		return result;
	}

}
