package org.noear.solon.extend.staticfiles;

/**
 * 静态文件位置
 *
 * @author noear
 * @since 1.0
 * */
public class StaticLocation {
    /**
     * 路径前缀
     * */
    public final String pathPrefix;
    /**
     * 资源仓库
     * */
    public final StaticRepository repository;

    public StaticLocation(String pathPrefix, StaticRepository repository) {
        this.pathPrefix = pathPrefix;
        this.repository = repository;
    }
}
