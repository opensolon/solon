package webapp.demoh_socketd;


import org.noear.nami.annotation.NamiClient;

@NamiClient("localrpc:/demoe/rpc/name")
public interface NameRpcService {
    String name(String name);
}
