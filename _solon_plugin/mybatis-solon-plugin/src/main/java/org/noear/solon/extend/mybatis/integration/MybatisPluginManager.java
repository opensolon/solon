package org.noear.solon.extend.mybatis.integration;

import org.apache.ibatis.plugin.Interceptor;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.Props;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author noear
 * @since 1.5
 */
public class MybatisPluginManager {
    private static List<Interceptor> interceptors;

    public static List<Interceptor> getInterceptors() {
        tryInit();

        return interceptors;
    }

    private static void tryInit() {
        if (interceptors != null) {
            return;
        }

        interceptors = new ArrayList<>();

        int index = 0;
        while (true) {
            Props props = Solon.cfg().getProp("mybatis.plugin[" + index + "]");
            if (props.size() == 0) {
                break;
            } else {
                index++;

                String name = null;
                for (Map.Entry kv : props.entrySet()) {
                    if (kv.getKey() instanceof String) {
                        String key = (String) kv.getKey();
                        if (key.endsWith(".class")) {
                            name = key.split("\\.")[0];
                        }
                    }
                }

                if (name != null) {
                    props = props.getProp(name);
                    Interceptor plugin = Utils.newInstance(props.get("class"));
                    if (plugin == null) {
                        throw new IllegalArgumentException("Mybatis.plugin[" + name + "].class load failed");
                    }
                    props.remove("class");

                    plugin.setProperties(props);
                    interceptors.add(plugin);
                }
            }
        }
    }
}
