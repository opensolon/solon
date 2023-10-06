package org.noear.solon.core.message;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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
public class ContextPathListener implements Listener {
    private final static Logger log = LoggerFactory.getLogger(ContextPathListener.class);

    private final String contextPath0;
    private final String contextPath1;
    private final boolean forced;

    public ContextPathListener(boolean forced) {
        this(Solon.cfg().serverContextPath(), forced);
    }

    /**
     * @param contextPath '/demo/'
     */
    public ContextPathListener(String contextPath, boolean forced) {
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
    public void onOpen(Session session) {
        if (contextPath0 != null) {
            if (session.pathNew().equals(contextPath0)) {
                //www:888 加 abc 后，仍可以用 www:888/abc 打开
                session.pathNew("/");
            } else if (session.pathNew().startsWith(contextPath1)) {
                session.pathNew(session.pathNew().substring(contextPath1.length() - 1));
            } else {
                if (forced) {
                    try {
                        session.close();
                    } catch (IOException e) {
                        log.warn(e.getMessage(), e);
                    }
                }
            }
        }
    }

    @Override
    public void onMessage(Session session, Message message) throws IOException {

    }
}
