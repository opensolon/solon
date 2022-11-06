package org.noear.nami.common;

import org.noear.nami.annotation.NamiClient;
import org.noear.solon.Utils;
import org.noear.solon.core.util.LogUtil;

/**
 * @author noear
 * @since 1.2
 * */
public class InfoUtils {
    public static void print(Class<?> type, NamiClient anno) {
        StringBuilder buf = new StringBuilder();
        buf.append("Bind the service ")
                .append(type.getTypeName());

        if (Utils.isNotEmpty(anno.url())) {
            buf.append(" to url(").append(anno.url()).append(")");
        }

        if (Utils.isNotEmpty(anno.name())) {
            buf.append(" to upstream(").append(anno.name()).append(")");
        }

        if (anno.upstream().length > 0) {
            buf.append(" to upstream(");
            for (String url : anno.upstream()) {
                buf.append(url).append(",");
            }
            buf.setLength(buf.length() - 1);
            buf.append(")");
        }

        LogUtil.global().warn("Nami: " + buf);
    }
}
