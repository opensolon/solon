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
        super.start(initialize);
    }
}
