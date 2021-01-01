package webapp.demoh_socketd;


import org.noear.nami.annotation.NamiClient;

@NamiClient("localrpc:/demoh/rpc")
public interface HelloRpcService {
    String hello(String name);
}
