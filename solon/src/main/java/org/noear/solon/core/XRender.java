package org.noear.solon.core;

/**
 * 通用渲染接口
 * */
public interface XRender {
    void render(Object object, XContext context) throws Exception;
}
