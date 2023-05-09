package org.noear.solon.web.staticfiles;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 静态文件映射
 *
 * @author noear
 * @since 1.0
 * */
public class StaticMappings {
    static final Map<StaticRepository, StaticLocation> locationMap = new HashMap<>();

    /**
     * 映射数量
     */
    public static int count() {
        return locationMap.size();
    }

    /**
     * 添加映射关系
     *
     * @param pathPrefix 路径前缀
     * @param repository 资源仓库
     */
    public synchronized static void add(String pathPrefix, StaticRepository repository) {
        addDo(pathPrefix, repository, false);
    }

    protected static void addDo(String pathPrefix, StaticRepository repository, boolean repositoryIncPrefix) {
        if (pathPrefix.startsWith("/") == false) {
            pathPrefix = "/" + pathPrefix;
        }

        //1.结尾不能自动加'/'; 2.使用 protected，允许用户同包名扩展

        locationMap.putIfAbsent(repository, new StaticLocation(pathPrefix, repository, repositoryIncPrefix));
    }

    /**
     * 移除仓库
     */
    public synchronized static void remove(StaticRepository repository) {
        locationMap.remove(repository);
    }

    /**
     * 查询静态资源
     */
    public static URL find(String path) throws Exception {
        URL rst = null;

        for (StaticLocation m : locationMap.values()) {
            if (path.startsWith(m.pathPrefix)) {
                if (m.repositoryIncPrefix) {
                    //path = /demo/file.htm
                    //relativePath = demo/file.htm （没有'/'开头）
                    rst = m.repository.find(path.substring(1));
                } else {
                    //path = /demo/file.htm
                    //relativePath = demo/file.htm （没有'/'开头）
                    if (m.pathPrefixAsFile) {
                        //如果是文件
                        int idx = m.pathPrefix.lastIndexOf("/");
                        rst = m.repository.find(m.pathPrefix.substring(idx + 1));
                    } else {
                        //如果是路段
                        rst = m.repository.find(path.substring(m.pathPrefix.length()));
                    }
                }

                if (rst != null) {
                    return rst;
                }
            }
        }

        return rst;
    }
}
