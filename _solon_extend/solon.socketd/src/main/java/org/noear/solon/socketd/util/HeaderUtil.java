package org.noear.solon.socketd.util;

import org.noear.solon.Utils;
import org.noear.solon.core.NvMap;

import java.util.Map;

/**
 * 头信息处理工具
 *
 * @author noear
 * @since 1.2
 * */
public class HeaderUtil {
    public static String encodeHeaderMap(Map<String, String> headers) {
        StringBuilder header = new StringBuilder();
        if (headers != null) {
            headers.forEach((k, v) -> {
                header.append(k).append("=").append(v).append("&");
            });

            if (header.length() > 0) {
                header.setLength(header.length() - 1);
            }
        }

        return header.toString();
    }

    public static Map<String, String> decodeHeaderMap(String header) {
        NvMap headerMap = new NvMap();

        if (Utils.isNotEmpty(header)) {
            String[] ss = header.split("&");
            for (String s : ss) {
                String[] kv = s.split("=");
                if (kv.length == 2) {
                    headerMap.put(kv[0], kv[1]);
                }
            }
        }

        return headerMap;
    }
}
