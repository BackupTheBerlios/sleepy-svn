
package sleepy.ssp.core;

import sleep.interfaces.*;
import sleep.runtime.*;

public interface SSPLoadable extends Loadable
{
	public boolean setup( SSPScript sspScript, SSPConnector sspConnector );
	public boolean tearDown( SSPScript sspScript, SSPConnector sspConnector );
}
