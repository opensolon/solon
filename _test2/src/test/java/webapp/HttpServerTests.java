package webapp;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.After;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServer;
import reactor.netty.tcp.TcpClient;

import java.nio.charset.Charset;

import static org.junit.Assert.assertThat;

public class HttpServerTests {
    private DisposableServer disposableServer;

    @After
    public void tearDown() {
        if (disposableServer != null) {
            disposableServer.onDispose().block();
        }
    }


    @Test
    public void httpPort() {
        disposableServer = HttpServer.create()
                .port(8080)
                .handle((req, resp) -> resp.sendNotFound())
                .wiretap(true)
                .bindNow();

    }


    @Test
    public void httpPortWithAddress() {
        disposableServer = HttpServer.create()
                .port(8080)
                .host("localhost")
                .handle((req, resp) -> resp.sendNotFound())
                .wiretap(true)
                .bindNow();
    }

    @Test
    public void releaseInboundChannelOnNonKeepAliveRequest() {
        disposableServer = HttpServer.create()
                .port(0)
                .handle((req, resp) -> req.receive().then(resp.status(200).send()))
                .wiretap(true)
                .bindNow();

        Flux<ByteBuf> src = Flux.range(0, 3)
                .map(n -> Unpooled.wrappedBuffer(Integer.toString(n)
                        .getBytes(Charset.defaultCharset())));

        Flux.range(0, 100)
                .concatMap(n -> HttpClient.create()
                        .port(disposableServer.address().getPort())
                        .tcpConfiguration(TcpClient::noSSL)
                        .wiretap(true)
                        .keepAlive(false)
                        .post()
                        .uri("/return")
                        .send(src)
                        .responseSingle((res, buf) -> Mono.just(res.status().code())))
                .collectList()
                .block();
    }
}