package org.noear.solon.docs;

/**
 * 文档类型
 *
 * @author noear
 * @since 2.4
 */
public enum DocType {
    SWAGGER_2("2.0"),
    SWAGGER_3("3.0");

    String version;

    DocType(String version) {
        this.version = version;
    }

    /**
     * 获取版本号
     * */
    public String getVersion() {
        return version;
    }
}
