package webapp.demoh_xsocket;

import org.noear.fairy.annotation.FairyClient;

@FairyClient("localrpc:/demoe/rpc")
public interface HelloRpcService {
    String hello(String name);
}
