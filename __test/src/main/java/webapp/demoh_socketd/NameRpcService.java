package webapp.demoh_socketd;


import org.noear.nami.annotation.NamiClient;

@NamiClient(name = "localrpc", path = "/demoh/rpc/name")
public interface NameRpcService {
    String name(String name);
}
