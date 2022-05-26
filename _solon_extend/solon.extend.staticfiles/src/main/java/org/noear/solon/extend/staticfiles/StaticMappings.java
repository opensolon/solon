package org.noear.solon.extend.staticfiles;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 静态文件印射
 *
 * @author noear
 * @since 1.0
 * */
public class StaticMappings {
    static final List<StaticLocation> locationList = new ArrayList<>();

    /**
     * 印射数量
     */
    public static int count() {
        return locationList.size();
    }

    /**
     * 添加印射关系
     *
     * @param pathPrefix 路径前缀
     * @param repository 资源仓库
     */
    public static void add(String pathPrefix, StaticRepository repository) {
        add(pathPrefix, true, repository);
    }

    /**
     * 添加印射关系
     *
     * @param pathPrefix          路径前缀
     * @param repositoryIncPrefix 资源仓库是否包括路径前缀
     * @param repository          资源仓库
     */
    public static void add(String pathPrefix, boolean repositoryIncPrefix, StaticRepository repository) {
        if (pathPrefix.startsWith("/") == false) {
            pathPrefix = "/" + pathPrefix;
        }

        if (pathPrefix.endsWith("/") == false) {
            pathPrefix = pathPrefix + "/";
        }

        locationList.add(new StaticLocation(pathPrefix, repository, repositoryIncPrefix));
    }

    /**
     * 查询静态资源
     */
    public static URL find(String path) throws Exception {
        URL rst = null;

        for (StaticLocation m : locationList) {
            if (path.startsWith(m.pathPrefix)) {
                if (m.repositoryIncPrefix) {
                    rst = m.repository.find(path);
                } else {
                    rst = m.repository.find(path.substring(m.pathPrefix.length()));
                }

                if (rst != null) {
                    return rst;
                }
            }
        }

        return rst;
    }
}
