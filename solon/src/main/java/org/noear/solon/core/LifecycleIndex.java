package org.noear.solon.core;

/**
 * 生命周期手动索引
 *
 * @author noear
 * @since 2.7
 */
public interface LifecycleIndex {
    int class_condition_if_missing = -980;
    int method_condition_if_missing = -970;
    int collection_inject = -960;
    int plugin_bean_uses = -950;
    int gateway_bean_uses = -940;
}
