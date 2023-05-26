package com.drools.solon.config;

/**
 * @author <a href="mailto:hongwen0928@outlook.com">Karas</a>
 * @date 2019/9/26 16:26
 * @since 1.0.0
 */
public class DroolsProperties {

    /**
     * 规则文件和决策表目录，多个目录使用逗号分割
     */
    private String path;

    /**
     * 轮询周期 - 单位：秒
     */
    private Long update;

    /**
     * 模式，stream 或 cloud
     */
    private String mode;

    /**
     * 是否开启监听器：on = 开；off = 关闭
     */
    private String listener;

    /**
     * 自动更新：on = 开；off = 关闭
     */
    private String autoUpdate;

    /**
     * 是否开启规则
     */
    private String verify;

    /**
     * 规则文件编码类型
     */
    private String charset;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getUpdate() {
        return update;
    }

    public void setUpdate(Long update) {
        this.update = update;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getListener() {
        return listener;
    }

    public void setListener(String listener) {
        this.listener = listener;
    }

    public String getAutoUpdate() {
        return autoUpdate;
    }

    public void setAutoUpdate(String autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    public String getVerify() {
        return verify;
    }

    public void setVerify(String verify) {
        this.verify = verify;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}
