package org.noear.solon.core;

/**
 * 生命周期手动索引
 *
 * @author noear
 * @since 2.7
 */
public interface LifecycleIndex {
    int CLASS_CONDITION_IF_MISSING = -980;
    int METHOD_CONDITION_IF_MISSING = -970;
    int COLLECTION_INJECT = -960;
    int PLUGIN_BEAN_USES = -950;
    int GATEWAY_BEAN_USES = -940;
}
