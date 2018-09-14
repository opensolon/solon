package org.noear.solonboot.router;

import com.sun.istack.internal.NotNull;
import org.noear.solonboot.XRouter;
import org.noear.solonboot.protocol.XContext;
import org.noear.solonboot.protocol.XEndpoint;
import org.noear.solonboot.protocol.XMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/*规则路由，功能强性能弱*/
public class XExpRouter<T> implements XRouter<T> {
    private List<XListener<T>> routerList = new ArrayList<>();

    ///////////////////////////////////////////////
    @Override
    public void add(@NotNull String path, T handler) {
        add(path, XEndpoint.main, XMethod.ALL, handler);
    }
    @Override
    public void add(@NotNull String path, int type, String method, T handler) {
        //path = prefixPath(path);

        path = path.replaceAll("\\.","\\\\.");

        //做开始预处理
        if (path.startsWith("/")) {
            path = "^" + path;
        }

        //做结束预处理
        if (path.endsWith("**")) {
            path = path.substring(0, path.length() - 2); //不限内容
        } else {
            if (path.endsWith("$") == false) { //结束不要加
                path = path + "$";
            }
        }

        //替换*值
        path = path.replaceAll("\\*", "[^/]+");

        routerList.add(new XListener(path, type, method, handler));
    }

    @Override
    public T matched(XContext context) {
        return matched(context, XEndpoint.main);
    }
    @Override
    public T matched(XContext context, int type) {
        String path = context.path();
        String method = context.method();

        for (XListener<T> l : routerList) {
            if (l.matches(type, method, path)) {
                return l.handler;
            }
        }

        return null;
    }

    class XListener<T> {
        public XListener(String pathExpr, int type, String method, T handler) {
            this.rule = Pattern.compile(pathExpr, Pattern.CASE_INSENSITIVE);
            this.type = type;
            this.method = method;
            this.handler = handler;
        }

        private int type;
        private Pattern rule;
        private String method;
        public T handler;

        public boolean matches(int type, String method, String path) {
            if (this.type == type) {
                if (this.method == XMethod.ALL) {
                    if (XMethod.isAll(method)) {
                        return rule.matcher(path).find();
                    }
                } else {
                    if (method.equals(this.method)) {
                        return rule.matcher(path).find();
                    }
                }
            }
            return false;
        }
    }
}
