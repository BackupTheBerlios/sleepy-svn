
package sleepy.ssp.core;

/**
 * SSPContext
 * -------------------------------
 *
 * @author Ralph Becker
 */
public interface SSPContext
{
	public static final String SSP_SCRIPTLOADER = "sleepy.ssp.SSPScriptLoader";
	public static final String SSP_SCRIPTCACHE = "sleepy.ssp.SSPScriptCache";
	
	public void addEnvironmentScript( String script );
}
