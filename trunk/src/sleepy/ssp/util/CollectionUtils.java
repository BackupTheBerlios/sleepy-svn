
package sleepy.ssp.util;

import sleep.engine.types.*;
import sleep.runtime.*;

import java.util.*;

/**
 * CollectionUtils
 * -------------------------------
 *
 * @author Ralph Becker
 */
public class CollectionUtils
{
	private CollectionUtils() { /* no instance required */ }

	public static Collection enumToCollection( Enumeration e )
	{
		ArrayList result = new ArrayList();
		while( e.hasMoreElements() )
		{
			result.add( e.nextElement() );
		}
		return result;
	}

	public static Object[] enumToArray( Enumeration e )
	{
		return enumToCollection(e).toArray();
	}
	
}
