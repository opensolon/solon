package org.noear.solon.extend.staticfiles;

/**
 * 静态文件位置
 *
 * @author noear
 * @since 1.0
 * */
public class StaticLocation {
    public final String start;
    public final StaticRepository repository;

    public StaticLocation(String start, StaticRepository repository) {
        this.start = start;
        this.repository = repository;
    }
}
