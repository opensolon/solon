package webapp.demoh_xsocket;

import org.noear.fairy.annotation.FairyClient;

@FairyClient("localrpc:/demoe/rpc/name")
public interface NameRpcService {
    String name(String name);
}
