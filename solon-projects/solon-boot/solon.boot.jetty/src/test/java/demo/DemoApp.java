package demo;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.noear.solon.Solon;

/**
 * @author noear 2022/9/6 created
 */
public class DemoApp {
    public static void main(String[] args){
        Solon.start(DemoApp.class, args, app->{
            app.onEvent(Server.class, server -> {
                ServerConnector connector = (ServerConnector)server.getConnectors()[0];
                //...
                //connector.set
            });
        });
    }
}
