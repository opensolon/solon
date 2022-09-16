package webapp.demoh_socketd;


import org.noear.nami.annotation.NamiClient;

@NamiClient(name = "localrpc", path = "/demoh/rpc")
public interface HelloRpcService {
    String hello(String name);
}
