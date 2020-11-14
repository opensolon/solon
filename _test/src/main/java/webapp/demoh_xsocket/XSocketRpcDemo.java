package webapp.demoh_xsocket;

import org.noear.solon.annotation.XBean;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XMethod;

@XMapping(value = "/demoe/rpc", method = XMethod.SOCKET)
@XBean(remoting = true)
public class XSocketRpcDemo implements XSocketRpc {
    @XMapping(value = "*", method = XMethod.SOCKET, before = true)
    public void bef(XContext ctx) {
        ctx.headerSet("Content-Type","test/json");
    }

    public String hello(String name) {
        return "name=" + name;
    }
}
