package org.apache.shardingsphere.solon;

import org.noear.snack.ONode;

import java.util.*;

/**
 *
 *
 * @author noear
 * @since 2.2
 */
public class YamlUtil {
    public static String toYamlString(Properties properties) {
        return toYamlString(ONode.load(properties));
    }

    public static String toYamlString(Map map) {
        return toYamlString(ONode.load(map));
    }

    public static String toYamlString(ONode oNode) {
        StringBuilder buf = new StringBuilder(512);
        toYamlStringDo(oNode, buf, 0, false);

        return buf.toString();
    }

    private static void toYamlStringDo(ONode oNode, StringBuilder buf, int deep, boolean keep) {
        switch (oNode.nodeType()) {
            case Value: {
                buf.append(oNode.getString());
            }
            break;
            case Object: {
                for (Map.Entry<String, ONode> kv : oNode.obj().entrySet()) {
                    if (keep == false) {
                        buf.append("\r\n");
                        toYamlStringDeepDo(buf, deep);
                    }
                    buf.append(kv.getKey());
                    if (kv.getKey().toString().startsWith("!") == false) {
                        buf.append(": ");
                    }

                    toYamlStringDo(kv.getValue(), buf, deep + 1, false);
                }
            }
            break;
            case Array: {
                for (ONode v : oNode.ary()) {
                    buf.append("\r\n");
                    toYamlStringDeepDo(buf, deep);
                    buf.append("- ");
                    toYamlStringDo(v, buf, deep, true);
                }
            }
            break;
            default: {
                buf.append("null");
            }
            break;
        }
    }

    private static void toYamlStringDeepDo(StringBuilder buf, int deep) {
        while (deep > 0) {
            buf.append("  ");
            deep--;
        }
    }
}
