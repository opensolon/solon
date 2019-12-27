package webapp;

import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

import java.util.logging.Level;

public class RawApp {
    public static void main(String[] args) {
        DisposableServer server = HttpServer.create()   // Prepares an HTTP server ready for configuration
                .host("localhost")
                .port(8080)
                .handle((req,res)->{
                    return res.send(req.receive()
                            .retain());
                })
                .bindNow();


        server.onDispose().block();
    }
}
