package org.noear.solon.core;

/**
 * 通用渲染接口
 *
 * @author noear
 * @since 1.0
 * */
public interface XRender {
    void render(Object object, XContext context) throws Throwable;
}
