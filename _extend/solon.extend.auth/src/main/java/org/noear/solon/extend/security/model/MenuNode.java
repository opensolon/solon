package org.noear.solon.extend.security.model;

import java.util.Map;

/**
 * @author noear
 * @since 1.4
 */
public class MenuNode {
    /**
     * 名字（代号）
     * */
    String name;
    /**
     * 显示名
     * */
    String displayName;
    /**
     * 地址
     * */
    String url;
    /**
     * 窗口止录
     * */
    String target;
    /**
     * 图标
     * */
    String icon;
    /**
     * 元信息
     * */
    Map<String,String> meta;
}
