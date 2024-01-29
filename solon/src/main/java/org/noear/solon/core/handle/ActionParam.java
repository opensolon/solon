package org.noear.solon.core.handle;

/**
 * 处理参数
 *
 * @author noear
 * @since 2.7
 */
public class ActionParam {
    /**
     * 名字
     */
    public String name;
    /**
     * 默认值
     */
    public String defaultValue;

    /**
     * 必须输入
     */
    public boolean isRequiredInput;

    /**
     * 必须输入 Body
     */
    public boolean isRequiredBody;
    /**
     * 必须输入 Header
     */
    public boolean isRequiredHeader;
    /**
     * 必须输入 Cookie
     */
    public boolean isRequiredCookie;
    /**
     * 必须输入 Path
     */
    public boolean isRequiredPath;
}