
package sleepy.ssp;

import java.lang.reflect.*;

import org.apache.commons.logging.Log;

public class ProxyLogger implements InvocationHandler 
{
	private Log log;
	private String name;
	private String shortname;
	public ProxyLogger( String name, Log log )
	{
		this.log = log;
		this.name = name;
		shortname = name.substring(name.lastIndexOf(".") + 1);
	}
	
	public String toString()
	{
		return "ProxyLogger("+name+"): " + log.toString();
	}
	
	private Object message( Object message )
	{
		return shortname + " - " + String.valueOf( message );
	}
	
	public Object invoke( Object proxy, Method method, Object[] args )
	{
		if ( method.getName().equals("debug") )
		{
			if ( args.length > 1 )
				debug( message( args[0] ), (Throwable) args[1] );
			else
				debug( message( args[0] ) );
		}
		else if ( method.getName().equals("error") )
		{
			if ( args.length > 1 )
				error( message( args[0] ), (Throwable) args[1] );
			else
				error( message( args[0] ) );
		}
		else if ( method.getName().equals("fatal") )
		{
			if ( args.length > 1 )
				fatal( message( args[0] ), (Throwable) args[1] );
			else
				fatal( message( args[0] ) );
		}
		else if ( method.getName().equals("info") )
		{
			if ( args.length > 1 )
				info( message( args[0] ), (Throwable) args[1] );
			else
				info( message( args[0] ) );
		}
		else if ( method.getName().equals("trace") )
		{
			if ( args.length > 1 )
				trace( message( args[0] ), (Throwable) args[1] );
			else
				trace( message( args[0] ) );
		}
		else if ( method.getName().equals("warn") )
		{
			if ( args.length > 1 )
				warn( message( args[0] ), (Throwable) args[1] );
			else
				warn( message( args[0] ) );
		}
		else if ( method.getName().equals("isDebugEnabled") )
		{
			return new Boolean( isDebugEnabled() );
		}
		else if ( method.getName().equals("isErrorEnabled") )
		{
			return new Boolean( isErrorEnabled() );
		}
		else if ( method.getName().equals("isFatalEnabled") )
		{
			return new Boolean( isFatalEnabled() );
		}
		else if ( method.getName().equals("isInfoEnabled") )
		{
			return new Boolean( isInfoEnabled() );
		}
		else if ( method.getName().equals("isTraceEnabled") )
		{
			return new Boolean( isTraceEnabled() );
		}
		else if ( method.getName().equals("isWarnEnabled") )
		{
			return new Boolean( isWarnEnabled() );
		}
		else if ( method.getName().equals("toString") )
		{
			return toString();
		}
		return null;
	}

	public void debug(Object message)
	{
		log.debug( message );
	}

	public void debug(Object message, Throwable t)
	{
		log.debug( message, t );
	}

	public void error(Object message)
	{
		log.error( message );
	}

	public void error(Object message, Throwable t)
	{
		log.error( message , t);
	}
	
	public void fatal(Object message)
	{
		log.fatal( message );
	}

	public void fatal(Object message, Throwable t)
	{
		log.fatal( message, t );
	}

	public void info(Object message)
	{
		log.info( message );
	}

	public void info(Object message, Throwable t)
	{
		log.info( message, t );
	}

	public void trace(Object message)
	{
		log.trace( message );
	}

	public void trace(Object message, Throwable t)
	{
		log.trace( message, t );
	}

	public void warn(Object message)
	{
		log.warn( message );
	}

	public void warn(Object message, Throwable t)
	{
		log.warn( message, t );
	}

	public boolean isDebugEnabled()
	{
		return log.isDebugEnabled();
	}
	
	public boolean isErrorEnabled()
	{
		return log.isDebugEnabled();
	}
	
	public boolean isFatalEnabled()
	{
		return log.isDebugEnabled();
	}
	
	public boolean isInfoEnabled()
	{
		return log.isInfoEnabled();
	}
	
	public boolean isTraceEnabled()
	{
		return log.isTraceEnabled();
	}
	
	public boolean isWarnEnabled()
	{
		return log.isWarnEnabled();
	}
}
