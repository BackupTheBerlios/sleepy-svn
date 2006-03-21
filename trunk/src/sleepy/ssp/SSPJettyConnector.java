
package sleepy.ssp;

import sleepy.ssp.core.SSPConnector;
import sleepy.ssp.util.*;
import sleep.runtime.Scalar;

import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;

import java.io.OutputStream;

public class SSPJettyConnector implements SSPConnector
{
	private HttpRequest request;
	private HttpResponse response;
	
	public SSPJettyConnector( HttpRequest request, HttpResponse response )
	{
		this.request = request;
		this.response = response;
	}
	
	public OutputStream getOutputStream()
	{
		return response.getOutputStream();
	}
	
	public Scalar getHeaders()
	{
    	return ReadOnlyScalar.wrap( SSPUtils.getHeadersFromHttpRequest(request) );
	}
}
