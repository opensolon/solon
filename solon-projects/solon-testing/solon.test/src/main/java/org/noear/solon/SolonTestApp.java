package org.noear.solon;

import org.noear.solon.core.NvMap;
import org.noear.solon.core.util.ConsumerEx;

/**
 * @author noear
 * @since 1.12
 */
public class SolonTestApp extends SolonApp {
    public SolonTestApp(Class<?> source, NvMap args) throws Exception {
        super(source, args.set("testing", "1"));
        Solon.appSet(this);
    }

    @Override
    public void start(ConsumerEx<SolonApp> initialize) throws Throwable {
        //默认关闭 http（避免主已经存在的服务端口冲突）
        enableHttp(false);
        super.start(initialize);
    }
}
