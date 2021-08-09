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
     * */
    public static void add(String start, StaticRepository repository) {
        locationList.add(new StaticLocation(start, repository));
    }

    /**
     * 查询静态资源
     * */
    public static URL find(String path) throws Exception {
        URL rst = null;

        for (StaticLocation m : locationList) {
            if (path.startsWith(m.start)) {
                rst = m.repository.find(path);

                if (rst != null) {
                    return rst;
                }
            }
        }

        return rst;
    }
}
