package webapp;

import org.noear.solon.XApp;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XUpstream;
import org.noear.solon.core.XUpstreamFactory;

public class TmpApp {
    public static void main(String[] args) {
        XApp.start(TmpApp.class, args, (app) -> {
            XUpstreamFactory bean = (name) -> {
                if ("user-service".equals(name)) {
                    return () -> "http://127.0.0.1:8080";
                } else {
                    return null;
                }
            };

            Aop.wrapAndPut(XUpstreamFactory.class, bean);
        });
    }
}
