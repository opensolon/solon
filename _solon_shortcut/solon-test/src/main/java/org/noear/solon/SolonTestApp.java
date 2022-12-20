package org.noear.solon;

import org.noear.solon.core.NvMap;
import org.noear.solon.core.util.ConsumerEx;

/**
 * @author noear
 * @since 1.11
 */
public class SolonTestApp extends SolonApp {
    public SolonTestApp(Class<?> source, NvMap args) throws Exception {
        super(source, args);
    }

    public SolonTestApp(Class<?> source, String[] args) throws Exception {
        super(source, NvMap.from(args));
    }

    public void start() throws Throwable {
        super.start(null);
    }
}
