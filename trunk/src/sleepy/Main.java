
package sleepy;

import sleepy.ssp.*;

import org.mortbay.util.*;
import org.mortbay.http.*;
import org.mortbay.http.handler.*;

public class Main {
	
    public static void main(String[] args) throws Exception {
        // Create the server
        HttpServer server = new HttpServer();
          
        // Create a port listener
        //SocketListener listener=new SocketListener();
        SocketListener listener=new SocketListener( new InetAddrPort("127.0.0.1",8181) );
        //listener.setPort(8181);
        server.addListener(listener);
        
        // Create a context 
        SSPHttpContext context = new SSPHttpContext();
        context.setContextPath("/");
        context.setResourceBase("./root/");
        context.addEnvironmentScript("sleepy.sl");
        
        server.addContext(context);
        
        ResourceHandler stdHandler = new ResourceHandler();
        context.addHandler(stdHandler);
     
        // Start the http server
        server.start();
    }
}
