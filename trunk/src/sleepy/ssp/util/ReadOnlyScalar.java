
package sleepy.ssp.util;

import sleep.engine.types.*;	
import sleep.runtime.*;	

public class ReadOnlyScalar extends Scalar
{
	public ReadOnlyScalar( ScalarType value )
	{
		this.value = value;
	}

	public ReadOnlyScalar( ScalarArray array )
	{
		this.array = array;
	}
	
	public ReadOnlyScalar( ScalarHash hash )
	{
		this.hash  = hash;
	}
	
	public void setValue(ScalarType _value)
	{
		throw new RuntimeException("Read-Only-Scalar: could not assign value");
	}

	public void setValue(ScalarArray _array)
	{
		throw new RuntimeException("Read-Only-Scalar: could not assign value");
	}

	public void setValue(ScalarHash _hash)
	{
		throw new RuntimeException("Read-Only-Scalar: could not assign value");
	}

	public void setValue(Scalar newValue)
	{
		throw new RuntimeException("Read-Only-Scalar: could not assign value");
	}
	
	private static ScalarType NULL = SleepUtils.getEmptyScalar().getValue();
	
	public static ReadOnlyScalar wrapScalar( Scalar value )
	{
		Object object = value.objectValue();
		return wrap( object );
	}

	public static ReadOnlyScalar wrap( Object value )
	{
		if ( value instanceof Scalar )
			value = ((Scalar) value).objectValue();

		if ( value == null ) {
			return new ReadOnlyScalar( NULL );
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
		else {
			return new ReadOnlyScalar( new ObjectValue( value ) );
		}
	}
	
	public static ReadOnlyScalar wrapInt( Integer value )
	{
		return wrapInt( value.intValue() );
	}
	
	public static ReadOnlyScalar wrapInt( int value )
	{
		return new ReadOnlyScalar( new IntValue( value ) );
	}

	public static ReadOnlyScalar wrapLong( Long value )
	{
		return wrapLong( value.longValue() );
	}

	public static ReadOnlyScalar wrapLong( long value )
	{
		return new ReadOnlyScalar( new LongValue( value ) );
	}

	public static ReadOnlyScalar wrapDouble( Double value )
	{
		return wrapDouble( value.doubleValue() );
	}
	
	public static ReadOnlyScalar wrapDouble( double value )
	{
		return new ReadOnlyScalar( new DoubleValue( value ) );
	}

	public static ReadOnlyScalar wrapString( String value )
	{
		return new ReadOnlyScalar( new StringValue( value ) );
	}

	public static ReadOnlyScalar wrapScalarArray( ScalarArray value )
	{
		return new ReadOnlyScalar( value );
	}
	
	public static ReadOnlyScalar wrapScalarHash( ScalarHash value )
	{
		return new ReadOnlyScalar( value );
	}
	
	public static ReadOnlyScalar wrapObject( Object value )
	{
		return new ReadOnlyScalar( new ObjectValue( value ) );
	}

}
