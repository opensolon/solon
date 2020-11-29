package webapp.demoh_xsocket;


import org.noear.nami.annotation.NamiClient;

@NamiClient("localrpc:/demoe/rpc/name")
public interface NameRpcService {
    String name(String name);
}
