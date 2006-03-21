
/**
   Sleep Server Pages Parser
*/
package sleepy.ssp.core;

import java.io.*;

%%

%init{
    buffer = new StringBuffer();
    code = new StringBuffer();
%init}

%public
%class SSPParser
%function getSleepCode
%type String 

%{
 
    StringBuffer buffer;
    StringBuffer code;

	boolean skipNewline = false;    

	public static String getCode( String pagecode )
	{
		ByteArrayInputStream in = null;
		String result = "";
		try
		{
			in = new ByteArrayInputStream( pagecode.getBytes() );
			SSPParser pageParser = new SSPParser(in);
			while ( pageParser.getSleepCode() != "<EOF>" ) ;
			result = pageParser.code.toString();
		}
		catch ( IOException ioe )
		{
			ioe.printStackTrace();
		}
		finally { if ( in != null ) { try { in.close(); } catch ( IOException e ) { /* ignored */ } } }
		return result;
	}

	public static String getCodeFromFile( String pagefile )
	{
		FileInputStream in = null;
		String result = "";
		try
		{
			in = new FileInputStream( pagefile );
			SSPParser pageParser = new SSPParser(in);
			while ( pageParser.getSleepCode() != "<EOF>" ) ;
			result = pageParser.code.toString();
		}
		catch ( IOException ioe )
		{
			ioe.printStackTrace();
		}
		finally { if ( in != null ) { try { in.close(); } catch ( IOException e ) { /* ignored */ } } }
		return result;
	}

	public void clear()
	{
		code.delete( 0, code.length() );
		buffer.delete( 0, code.length() );
	}
	
	public static void main(String[] args)
	{

		if (args.length > 0)
		{
			System.out.println(SSPParser.getCodeFromFile(args[0]));
		}
        	System.exit(0);
	}

	
%}

%line
%column
%char

%full
%states SCRIPT_BLOCK_AHEAD VAR_OUT_AHEAD

SCRIPT_START=\<\$
SCRIPT_END=\$\>
SCRIPT_VAR_OUT=\<\$\=
NEWLINE=\r|\n|\r\n

%%

<YYINITIAL> {

  {SCRIPT_START} {
      code.append( buffer.toString() );
	  buffer.delete(0,buffer.length());
      yybegin(SCRIPT_BLOCK_AHEAD);
	  //skipNewline = true;
	  skipNewline = false;
  }

  {SCRIPT_VAR_OUT} {
      buffer.append("' . ");
      
  	  yybegin(VAR_OUT_AHEAD);
  }

  {SCRIPT_END} {
		throw new RuntimeException("illegal statement: " + yytext() + " at line: "+yyline+" column:"+yycolumn);
  }
  
  {NEWLINE} {
  	  if ( skipNewline )
  	  {
  	  	skipNewline = false;
  	  }
  	  else if ( buffer.length() > 0 )
  	  {
  	    code.append( "output('" );
  	    code.append( buffer.toString() );
  	    buffer.delete(0,buffer.length());
		code.append( "');" ).append( "\n" );
  	  }
  	  //else code.append( yytext() );
  	  else code.append( "\n" );
  }

  . { 
  	  buffer.append( yytext() );
  }
}

<SCRIPT_BLOCK_AHEAD> {
	{SCRIPT_START} {
		throw new RuntimeException("illegal statement: " + yytext() + " at line: "+yyline+" column:"+yycolumn);
	}
	{SCRIPT_VAR_OUT} {
		throw new RuntimeException("illegal statement: " + yytext() + " at line: "+yyline+" column:"+yycolumn);
	}
	{SCRIPT_END} {
		code.append( buffer.toString() );
		buffer.delete(0,buffer.length());
		//skipNewline = true;
		skipNewline = false;
	    yybegin(YYINITIAL);
	}
	{NEWLINE} {
  	  if ( skipNewline ) {
  	  	skipNewline = false;
	  }
  	  else {
        	//buffer.append( yytext() );
        	buffer.append( "\n" );
	  }
    }
	. { buffer.append( yytext() ); }
}

<VAR_OUT_AHEAD> {
	{SCRIPT_START} {
		throw new RuntimeException("illegal statement: " + yytext() + " at line: "+yyline+" column:"+yycolumn);
	}
	{SCRIPT_VAR_OUT} {
		throw new RuntimeException("illegal statement: " + yytext() + " at line: "+yyline+" column:"+yycolumn);
	}
	{SCRIPT_END} {
		buffer.append(" . '");
		yybegin(YYINITIAL); 
	}
	{NEWLINE} {
		throw new RuntimeException("$> expected at line: "+yyline+"column:"+yycolumn);
    }
	. { buffer.append( yytext() ); }
}

<<EOF>> {
	if ( zzLexicalState == SCRIPT_BLOCK_AHEAD || zzLexicalState == SCRIPT_BLOCK_AHEAD )
		throw new RuntimeException("invalid code due to premature end of page");
	else
	{
		code.append( buffer.toString() );
		//System.out.println( code.toString() );
	}
    return "<EOF>";
}

