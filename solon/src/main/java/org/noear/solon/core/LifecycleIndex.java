package org.noear.solon.core;

/**
 * 生命周期手动索引
 *
 * @author noear
 * @since 2.7
 */
public interface LifecycleIndex {
    int CLASS_CONDITION_IF_MISSING = -98;
    int METHOD_CONDITION_IF_MISSING = -97;
    int PLUGIN_BEAN_USES = -96;
    int COLLECTION_INJECT_USES = -95;
    int GATEWAY_BEAN_USES = -94;
}
