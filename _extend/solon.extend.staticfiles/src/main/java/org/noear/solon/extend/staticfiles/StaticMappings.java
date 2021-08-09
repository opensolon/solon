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
     * 添加印射关系
     */
    public static void add(String pathPrefix, StaticRepository repository) {
        locationList.add(new StaticLocation(pathPrefix, repository));
    }

    /**
     * 查询静态资源
     */
    public static URL find(String path) throws Exception {
        URL rst = null;

        for (StaticLocation m : locationList) {
            if (path.startsWith(m.pathPrefix)) {
                rst = m.repository.find(path);

                if (rst != null) {
                    return rst;
                }
            }
        }

        return rst;
    }
}
