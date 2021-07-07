package org.noear.solon.core;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.ext.LinkedCaseInsensitiveMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 可排序，不区分大小写（Name data map）
 *
 * 用于：Attr 处理
 *
 * @see Context#attrMap()
 * @author noear
 * @since 1.3
 * */
public class NdMap extends LinkedCaseInsensitiveMap<Object> {

    public NdMap() {
        super();
    }

    public NdMap(Map map) {
        super();
        map.forEach((k, v) -> {
            put(k.toString(), v);
        });
    }
}
