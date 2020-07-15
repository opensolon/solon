package org.noear.solon.core;

/**
 * 路由监听器（为路由器服务）
 * */
public class XListener {
    public XListener(String path, XMethod method, int index, XHandler handler) {
        _p = path;
        _pr = new PathAnalyzer(path);
        _m = method;

        this.index = index;
        this.handler = handler;
    }

    public final int index; //顺序
    public final XHandler handler;//代理

    private final String _p; //path
    private final PathAnalyzer _pr; //path rule 规则
    private final XMethod _m; //方式


    /**
     * 是否匹配
     * */
    public boolean matches(XMethod method2, String path2) {
        if (XMethod.ALL.code == _m.code) {
            return do_matches(path2);
        } else if (XMethod.HTTP.code == _m.code) { //不是null时，不能用==
            if (method2.isHttpMethod()) {
                return do_matches(path2);
            }
        } else if (method2.code == _m.code) {
            return do_matches(path2);
        }

        return false;
    }

    private boolean do_matches(String path2){
        if("**".equals(_p)){
            return true;
        }

        if(_p.equals(path2)){
            return true;
        }

        return _pr.matches(path2);
    }
}
