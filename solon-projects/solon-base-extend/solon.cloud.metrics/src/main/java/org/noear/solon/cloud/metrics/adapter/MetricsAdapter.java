package org.noear.solon.cloud.metrics.adapter;

import org.noear.solon.core.AopContext;

/**
 * @author noear
 */
public interface MetricsAdapter {
    void adaptive(AopContext aopContext);
}
