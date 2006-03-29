
package sleepy.ssp.core;

import sleepy.ssp.util.*;

import sleep.runtime.*;
import sleep.engine.*;
import sleep.error.*;

import java.io.*;
import java.util.*;

/**
 * SSPErrorScript
 * -------------------------------
 *
 * @author Ralph Becker
 */
public class SSPErrorScript extends SSPScript
{
	public SSPErrorScript( YourCodeSucksException ycse, Hashtable env )
	{
		super( env );
		installBlock( errorsToBlock( ycse ) );
	}
	
	public SSPErrorScript( String message, Hashtable env )
	{
		super( env );
		installBlock( messageToBlock( message ) );
	}
	
	public void setName( String name )
	{
		this.name = name;
		variables.putScalar( "$pathInContext", SleepUtils.getScalar( name ) );
	}
	
	protected Block errorsToBlock( YourCodeSucksException ycse )
	{
		Iterator i = ycse.getErrors().iterator();
		StringBuffer buffer = new StringBuffer("");
		getHead( buffer, "Error" );
		buffer.append("writeln('<h1>YourCodeSucksException for: ' . $pathInContext . '</h1>');\n");
		while (i.hasNext())
		{
			buffer.append("writeln('<p>');\n");
			SyntaxError error = (SyntaxError)i.next();
			buffer.append("writeln('line ").append(error.getLineNumber()).append(": ").append( escape(error.getDescription())).append("<br>');\n");
			buffer.append("writeln('  ").append(escape(error.getCodeSnippet())).append("<br>');\n");
			if ( error.getMarker() != null )
				buffer.append("writeln('  ").append(error.getMarker()).append("<br>');\n");
			buffer.append("writeln('</p>');\n");
		}
		getFoot( buffer, null );
		//return (Block) BlockUtils.compile( buffer.toString() );
		Object result = BlockUtils.compile( buffer.toString() );
		if ( result instanceof Block )
			return (Block) result;
		else 
			processScriptErrors( (YourCodeSucksException) result );
		return (Block) null;
	}
	
	protected Block messageToBlock( String message )
	{
		StringBuffer buffer = new StringBuffer("");
		getHead( buffer, "Error" );
		buffer.append("writeln('<h1>").append(message).append(" ' . ").append("$pathInContext").append(" . '</h1>');\n");
		getFoot( buffer, null );
		//return (Block) BlockUtils.compile( buffer.toString() );
		Object result = BlockUtils.compile( buffer.toString() );
		if ( result instanceof Block )
			return (Block) result;
		else 
			processScriptErrors( (YourCodeSucksException) result );
		return (Block) null;
	}

	protected void getHead( StringBuffer buffer, String title )
	{
		buffer.append("writeln('<html>');\n");
		buffer.append("writeln('  <head>');\n");
		buffer.append("writeln('  	<title>").append(title).append("</title>');\n");
		buffer.append("writeln('  </head>');\n");
		buffer.append("writeln('  <body>');\n");
	}
	
	protected void getFoot( StringBuffer buffer, String message )
	{
		if ( message != null ) 
		{
			buffer.append("writeln('    <hr>');\n");
			buffer.append("writeln('    <p>');\n");
			buffer.append("writeln('      ").append(message).append("');\n");
			buffer.append("writeln('    </p>');\n");
		}
		buffer.append("writeln('  </body>');\n");
		buffer.append("writeln('</html>');\n");
	}
	
	protected String escape( String code )
	{
		//return code.replaceAll("'","\\\'");
//		Stack stack = new Stack();
//		stack.push( SleepUtils.getScalar("\\\\'") );
//		stack.push( SleepUtils.getScalar("\\\'") );
//		stack.push( SleepUtils.getScalar(code) );
//		String result = callFunction("&replace", stack ).stringValue();
		String result = code.replaceAll("\\\'","\\\\'");
		System.out.println( "escape.code:   " + code );
		System.out.println( "escape.result: " + result );
		return result;
		//return callFunction("&replace", stack ).stringValue();
	}
	
	private void processScriptErrors(YourCodeSucksException ex)
	{
		LinkedList errors = ex.getErrors();
		Iterator i = errors.iterator();
		System.out.println("YourCodeSucksException:");
		while (i.hasNext())
		{
			SyntaxError anError = (SyntaxError)i.next();
			StringBuffer buffer = new StringBuffer("Error: ");
			buffer.append(anError.getDescription()).append(" at line ").append(anError.getLineNumber());
			buffer.append("\n");
			buffer.append("      ").append(anError.getCodeSnippet());
			buffer.append("\n");
			if (anError.getMarker() != null)
				buffer.append("      " + anError.getMarker());
			System.out.println( buffer.toString() );
		}
	}

}
