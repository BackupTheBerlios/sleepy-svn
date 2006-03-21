
package sleepy.ssp.core;

import java.io.OutputStream;

import sleep.runtime.Scalar;

public interface SSPConnector
{
	public OutputStream getOutputStream();
	public Scalar getHeaders();
}
