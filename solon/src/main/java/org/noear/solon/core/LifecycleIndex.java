package org.noear.solon.core;

/**
 * 生命周期手动索引
 *
 * @author noear
 * @since 2.7
 */
public interface LifecycleIndex {
    int CLASS_CONDITION_IF_MISSING = -99;
    int METHOD_CONDITION_IF_MISSING = -98;
    int PLUGIN_BEAN_BUILD = -97;
    int PARAM_COLLECTION_INJECT = -96;
    int FIELD_COLLECTION_INJECT = -95;
    int PLUGIN_BEAN_USES = -94;
    int GATEWAY_BEAN_USES = -93;
}
