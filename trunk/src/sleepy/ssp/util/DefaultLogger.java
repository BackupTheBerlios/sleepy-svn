
package sleepy.ssp.util;

import java.lang.reflect.*;
import java.util.*;
import java.text.*;

public class DefaultLogger implements Logger
{
	protected int currentLogLevel = 3; // DefaultLogger.INFO

	protected String logName = "sleepy.ssp.util.DefaultLogger";
	protected String shortLogName = null;
	protected final String defaultDateFormat = "yyyy/MM/dd HH:mm:ss:SSS zzz";
	protected SimpleDateFormat dateFormatter = new SimpleDateFormat(defaultDateFormat);

    protected boolean showLogName = false;
    protected boolean showShortName = true;
    protected boolean showDateTime = false;

    public static final int ALL  = 0;
    public static final int TRACE  = 1;
    public static final int DEBUG  = 2;
    public static final int INFO   = 3;
    public static final int WARN   = 4;
    public static final int ERROR  = 5;
    public static final int FATAL  = 6;
	public static final int OFF    = 7;

	public DefaultLogger( Class clazz )
	{
		logName = clazz.getName();
	}

	public DefaultLogger( String name )
	{
		logName = name;
	}

    protected boolean isLevelEnabled(int logLevel) {
        return (logLevel >= currentLogLevel);
    }

	public void setLogLevel( int level )
	{
		if ( level >= 0 && level <= 7 )
			currentLogLevel = level;
	}

	public void setDateFormat( String dateTimeFormat )
	{
		if ( dateTimeFormat == null || "".equals(dateTimeFormat) )
		{
			dateFormatter = new SimpleDateFormat(defaultDateFormat);
			return;
		}
		try
		{
			dateFormatter = new SimpleDateFormat(dateTimeFormat);
		}
		catch(IllegalArgumentException e)
		{
			dateFormatter = new SimpleDateFormat(defaultDateFormat);
		}
	}

	public void showLogName( boolean on_off )
	{
		showLogName = on_off;
		if ( !showLogName )
			showShortName = true;
		else
			showShortName = false;
	}

	public void showDateTime( boolean on_off )
	{
		showDateTime = on_off;
	}	

    protected void log(int type, Object message, Throwable t)
    {
		if ( !isLevelEnabled(type) ) return;
		
        StringBuffer buf = new StringBuffer();

        if(showDateTime)
        {
            buf.append(dateFormatter.format(new Date()));
            buf.append(" ");
        }

        switch(type)
        {
            case DefaultLogger.TRACE: buf.append("[TRACE] "); break;
            case DefaultLogger.DEBUG: buf.append("[DEBUG] "); break;
            case DefaultLogger.INFO:  buf.append("[INFO] ");  break;
            case DefaultLogger.WARN:  buf.append("[WARN] ");  break;
            case DefaultLogger.ERROR: buf.append("[ERROR] "); break;
            case DefaultLogger.FATAL: buf.append("[FATAL] "); break;
            default:
        }

        // Append the name of the log instance if so configured
 		if( showShortName) {
            if( shortLogName==null ) {
                // Cut all but the last component of the name for both styles
                shortLogName = logName.substring(logName.lastIndexOf(".") + 1);
                shortLogName =
                    shortLogName.substring(shortLogName.lastIndexOf("/") + 1);
            }
            buf.append(String.valueOf(shortLogName)).append(" - ");
        } else if(showLogName) {
            buf.append(String.valueOf(logName)).append(" - ");
        }

        // Append the message
        buf.append(String.valueOf(message));

        // Append stack trace if not null
        if(t != null) {
            buf.append(" <");
            buf.append(t.toString());
            buf.append(">");

            java.io.StringWriter sw= new java.io.StringWriter(1024);
            java.io.PrintWriter pw= new java.io.PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            buf.append(sw.toString());
        }

        write(buf);

    }

    protected void write(StringBuffer buffer)
    {
        System.err.println(buffer.toString());
    }

	public void debug(Object message)
	{
		log( DefaultLogger.DEBUG, message, null );
	}

	public void debug(Object message, Throwable t)
	{
		log( DefaultLogger.DEBUG, message, t );
	}

	public void error(Object message)
	{
		log( DefaultLogger.ERROR, message, null );
	}

	public void error(Object message, Throwable t)
	{
		log( DefaultLogger.ERROR, message, t );
	}
	
	public void fatal(Object message)
	{
		log( DefaultLogger.FATAL, message, null );
	}

	public void fatal(Object message, Throwable t)
	{
		log( DefaultLogger.FATAL, message, t );
	}

	public void info(Object message)
	{
		log( DefaultLogger.INFO, message, null );
	}

	public void info(Object message, Throwable t)
	{
		log( DefaultLogger.INFO, message, t );
	}

	public void trace(Object message)
	{
		log( DefaultLogger.TRACE, message, null );
	}

	public void trace(Object message, Throwable t)
	{
		log( DefaultLogger.TRACE, message, t );
	}


	public void warn(Object message)
	{
		log( DefaultLogger.WARN, message, null );
	}

	public void warn(Object message, Throwable t)
	{
		log( DefaultLogger.WARN, message, t );
	}


	public boolean isDebugEnabled()
	{
		return isLevelEnabled( DefaultLogger.DEBUG );
	}
	
	public boolean isErrorEnabled()
	{
		return isLevelEnabled( DefaultLogger.ERROR );
	}
	
	public boolean isFatalEnabled()
	{
		return isLevelEnabled( DefaultLogger.FATAL );
	}
	
	public boolean isInfoEnabled()
	{
		return isLevelEnabled( DefaultLogger.INFO );
	}
	
	public boolean isTraceEnabled()
	{
		return isLevelEnabled( DefaultLogger.TRACE );
	}
	
	public boolean isWarnEnabled()
	{
		return isLevelEnabled( DefaultLogger.WARN );
	}
	
}
