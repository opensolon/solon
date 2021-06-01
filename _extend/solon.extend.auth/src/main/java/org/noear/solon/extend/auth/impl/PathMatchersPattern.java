package org.noear.solon.extend.auth.impl;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.PathAnalyzer;
import org.noear.solon.extend.auth.PathMatchers;

import java.util.ArrayList;
import java.util.List;

/**
 * 模式路由匹配器
 *
 * @author noear
 * @since 1.4
 */
public class PathMatchersPattern implements PathMatchers {
    /**
     * 拦截路由
     */
    private List<PathAnalyzer> includeList = new ArrayList<>();

    /**
     * 放行路由
     */
    private List<PathAnalyzer> excludeList = new ArrayList<>();

    public PathMatchersPattern addInclude(String pattern) {
        includeList.add(PathAnalyzer.get(pattern));
        return this;
    }

    public PathMatchersPattern addExclude(String pattern) {
        excludeList.add(PathAnalyzer.get(pattern));
        return this;
    }

    @Override
    public boolean matches(Context ctx, String path) {
        //1.放行匹配
        for (PathAnalyzer pa : excludeList) {
            if (pa.matches(path)) {
                return false;
            }
        }

        //拦截匹配
        for (PathAnalyzer pa : includeList) {
            if (pa.matches(path)) {
                return true;
            }
        }

        //什么都没匹配上
        return false;
    }
}
