package org.noear.solon.extend.impl;

import org.noear.snack.ONode;
import org.noear.solon.core.PropsConverter;

import java.util.Properties;

/**
 * @author noear
 * @since 1.6
 */
public class PropsConverterExt extends PropsConverter {
    @Override
    public <T> T convert(Properties props, T target, Class<T> targetClz) {
        if (target == null) {
            return ONode.loadObj(props).toObject(targetClz);
        } else {
            return ONode.loadObj(props).bindTo(target);
        }
    }
}
