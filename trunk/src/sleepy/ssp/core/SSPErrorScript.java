
package sleepy.ssp.core;

import sleepy.ssp.util.*;

import sleep.runtime.*;
import sleep.engine.*;
import sleep.error.*;

import java.io.*;
import java.util.*;

/**
 * 
 * -------------------------------
 *
 * @author Ralph Becker
 */
public class SSPErrorScript extends SSPScript
{
	public SSPErrorScript( YourCodeSucksException ycse, File scriptfile, Hashtable env )
	{
		super( env );
		installBlock( errorsToBlock( ycse, scriptfile.toString() ) );
	}
	
	public SSPErrorScript( String message, File scriptfile, Hashtable env )
	{
		super( env );
		installBlock( messageToBlock( message, scriptfile.toString() ) );
	}
	
	protected Block errorsToBlock( YourCodeSucksException ycse, String scriptpath )
	{
		Iterator i = ycse.getErrors().iterator();
		StringBuffer buffer = new StringBuffer("");
		buffer.append("output('YourCodeSucksException for: ").append(scriptpath).append("');\n");
		while (i.hasNext())
		{
			SyntaxError error = (SyntaxError)i.next();
			buffer.append("output('line ").append(error.getLineNumber()).append(": ").append( error.getDescription()).append("');\n");
			buffer.append("output('  ").append(error.getCodeSnippet()).append("');\n");
			if ( error.getMarker() != null )
				buffer.append("output('  ").append(error.getMarker()).append("');\n");
		}
		return (Block) BlockUtils.compile( buffer.toString() );
	}
	
	protected Block messageToBlock( String message, String scriptpath )
	{
		return (Block) BlockUtils.compile( "output('" + message + " (" + scriptpath + ")');\n" );
	}

}
