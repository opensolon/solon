package org.noear.solon.net.socketd.listener;

import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.solon.core.util.PathAnalyzer;
import org.noear.solon.core.util.PathUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * 路径表过式监听器（自动解析出 /user/{id} 的值）
 *
 * @author noear
 * @since 2.0
 */
public class ExpressListener implements Listener {
    private Listener listener;

    //path 分析器
    private PathAnalyzer pathAnalyzer;//路径分析器
    //path key 列表
    private List<String> pathKeys;

    public ExpressListener(String path, Listener listener) {
        this.listener = listener;

        if (path != null && path.indexOf("{") >= 0) {
            path = PathUtil.mergePath(null, path);

            pathKeys = new ArrayList<>();
            Matcher pm = PathUtil.pathKeyExpr.matcher(path);
            while (pm.find()) {
                pathKeys.add(pm.group(1));
            }

            if (pathKeys.size() > 0) {
                pathAnalyzer = PathAnalyzer.get(path);
            }
        }
    }

    @Override
    public void onOpen(Session s) throws IOException{
        //获取path var
        if (pathAnalyzer != null) {
            Matcher pm = pathAnalyzer.matcher(s.path());
            if (pm.find()) {
                for (int i = 0, len = pathKeys.size(); i < len; i++) {
                    s.handshake().paramPut(pathKeys.get(i), pm.group(i + 1));//不采用group name,可解决_的问题
                }
            }
        }

        listener.onOpen(s);
    }

    @Override
    public void onMessage(Session s, Message m) throws IOException {
        listener.onMessage(s, m);
    }

    @Override
    public void onClose(Session s) {
        listener.onClose(s);
    }

    @Override
    public void onError(Session s, Throwable e) {
        listener.onError(s, e);
    }
}
