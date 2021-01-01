package webapp.demoh_socketd;


import org.noear.nami.annotation.NamiClient;

@NamiClient("localrpc:/demoh/rpc/name")
public interface NameRpcService {
    String name(String name);
}
