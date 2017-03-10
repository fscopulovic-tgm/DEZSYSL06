package windpark;

import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.webserver.WebServer;

public class ServerApplication {

    private int port;

    public ServerApplication(int port) {
        this.port = port;
        try {
            this.startServer();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void startServer() throws Exception {
        WebServer webserver = new WebServer(this.port);
        XmlRpcServer xmlRpcServer = webserver.getXmlRpcServer();
        PropertyHandlerMapping phm = new PropertyHandlerMapping();
        phm.addHandler("Windkraft", Windkraftanlage.class);
        xmlRpcServer.setHandlerMapping(phm);
        webserver.start();
        System.out.println("Windanlage hat gestartet");
    }
}

