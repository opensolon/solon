package org.noear.solon;

import org.noear.solon.core.XContext;
import org.noear.solon.core.XEndpoint;
import org.noear.solon.core.XMethod;
import sun.misc.Regexp;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * 通用路由器
 * */
public class XRouter<T> {
    private List<XListener<T>> _list = new ArrayList<>();


    /** 添加路由关系 */
    public void add( String path, T handler) {
        add(path, XEndpoint.main, XMethod.ALL, handler);
    }

    /** 添加路由关系 */
    public void add( String path, int type, String method, T handler) {
        _list.add(new XListener(XUtil.expCompile(path), type, method, handler));
    }

    /** 区配目标（根据上上文） */
    public T matchOne(XContext context, int type) {
        String path = context.path();
        String method = context.method();

        for (XListener<T> l : _list) {
            if (l.matches(type, method, path)) {
                return l.h;
            }
        }

        return null;
    }

    public List<T> matchAll(XContext context, int type) {
        String path = context.path();
        String method = context.method();

        return _list.stream()
                .filter(l->l.matches(type, method, path))
                .map(l->l.h)
                .collect(Collectors.toList());
    }

    //路由监听
    class XListener<T> {
        public XListener(String r,  int t, String m, T h) {
            this.r = Pattern.compile(r, Pattern.CASE_INSENSITIVE);
            this.t = t;
            this.m = m;
            this.h = h;
        }

        private int t; //类型
        private Pattern r; //规则
        private String m; //方式
        protected T h;//代理

        protected boolean matches(int t, String m, String p) {
            if (this.t == t) {
                if (XMethod.ALL.equals(this.m)) { //不是null时，不能用==
                    if (XMethod.isAll(m)) {
                        return r.matcher(p).find();
                    }
                } else {
                    if (m.equals(this.m)) {
                        return r.matcher(p).find();
                    }
                }
            }
            return false;
        }
    }
}
