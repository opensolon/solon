package org.noear.solon.net.http;

/**
 * Http 扩展
 *
 * @author noear
 * @since 2.9
 */
public interface HttpExtension {
    /**
     * 初始化
     */
    default void onInit(HttpUtils httpUtils, String url) {

    }
}
