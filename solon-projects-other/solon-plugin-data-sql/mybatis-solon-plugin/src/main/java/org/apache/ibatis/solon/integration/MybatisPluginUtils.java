package org.apache.ibatis.solon.integration;

import org.apache.ibatis.plugin.Interceptor;
import org.noear.solon.core.Props;
import org.noear.solon.core.util.ClassUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 插件解析工具
 *
 * @author noear
 * @since 1.10
 */
public class MybatisPluginUtils {
    /**
     * 解析
     *
     * @param prefix 配置前缀
     */
    public static List<Interceptor> resolve(Props configRoot, String prefix) {
        List<Interceptor> interceptors = new ArrayList<>();

        int index = 0;
        while (true) {
            Props props = configRoot.getProp(prefix + "[" + index + "]");
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
                    Interceptor plugin = ClassUtil.tryInstance(props.get("class"));
                    if (plugin == null) {
                        throw new IllegalArgumentException("Mybatis plugin [" + name + "].class load failed");
                    }
                    props.remove("class");

                    plugin.setProperties(props);
                    interceptors.add(plugin);
                }
            }
        }

        return interceptors;
    }
}
