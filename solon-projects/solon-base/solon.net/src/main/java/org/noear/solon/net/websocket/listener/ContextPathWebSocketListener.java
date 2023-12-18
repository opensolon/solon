package org.noear.solon.net.websocket.listener;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.net.websocket.WebSocket;

/**
 * 提供 ContextPath 类似的功能（优先级要极高）
 *
 * @author noear
 * @since 2.5
 */

/**
 * 提供 ContextPath 类似的功能（优先级要极高）
 *
 * @author noear
 * @since 2.5
 */
public class ContextPathWebSocketListener extends SimpleWebSocketListener {
    private final String contextPath0;
    private final String contextPath1;
    private final boolean forced;

    public ContextPathWebSocketListener() {
        this(Solon.cfg().serverContextPath(), Solon.cfg().serverContextPathForced());
    }

    /**
     * @deprecated 2.6
     * */
    @Deprecated
    public ContextPathWebSocketListener(boolean forced) {
        this(Solon.cfg().serverContextPath(), forced);
    }

    /**
     * @param contextPath '/demo/'
     */
    public ContextPathWebSocketListener(String contextPath, boolean forced) {
        this.forced = forced;

        if (Utils.isEmpty(contextPath)) {
            contextPath0 = null;
            contextPath1 = null;
        } else {
            String newPath = null;
            if (contextPath.endsWith("/")) {
                newPath = contextPath;
            } else {
                newPath = contextPath + "/";
            }

            if (newPath.startsWith("/")) {
                this.contextPath1 = newPath;
            } else {
                this.contextPath1 = "/" + newPath;
            }

            this.contextPath0 = contextPath1.substring(0, contextPath1.length() - 1);
        }
    }

    @Override
    public void onOpen(WebSocket s) {
        if (contextPath0 != null) {
            if (s.path().equals(contextPath0)) {
                //www:888 加 abc 后，仍可以用 www:888/abc 打开
                s.pathNew("/");
            } else if (s.path().startsWith(contextPath1)) {
                s.pathNew(s.path().substring(contextPath1.length() - 1));
            } else {
                if (forced) {
                    try {
                        s.close();
                    } catch (Exception e) {
                        LogUtil.global().warn("ContextPathListener onOpen failed!", e);
                    }
                }
            }
        }
    }
}
