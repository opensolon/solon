package com.layjava.docs.javadoc.solon.properties;

import cn.hutool.core.util.StrUtil;
import org.noear.solon.core.handle.Result;

import java.util.Arrays;
import java.util.List;

/**
 * 文档配置属性
 *
 * @author chengliang
 * @since  2024/04/16
 */
public class DocsProperty {

    /**
     * 是否开启接口文档
     */
    private Boolean enabled;

    /**
     * 分组名称
     */
    private String groupName;
    /**
     * 分组包路径
     */
    private String packageName;

    /**
     * 数据中全局响应
     */
    private Boolean globalResponseInData;

    /**
     * 全局数据类型
     */
    private Class globalData;

    /**
     * 标题
     */
    private String title;
    /**
     * 描述
     */
    private String description;
    /**
     * 版本
     */
    private String version;
    /**
     * 作者信息
     */
    private ContactProperty contact;

    /**
     * 支持的协议
     */
    private List<String> schemes;

    /**
     * 是不是开启的
     * 默认为开启
     *
     * @return boolean
     */
    public boolean isEnabled() {
        if (this.enabled == null) {
            return true;
        }
        return this.enabled;
    }

    public List<String> getSchemes() {
        return schemes == null ? Arrays.asList("http", "https") : schemes;
    }

    public void setSchemes(List<String> schemes) {
        this.schemes = schemes;
    }

    public Boolean getGlobalResponseInData() {
        return globalResponseInData == null ? true : globalResponseInData;
    }

    public void setGlobalResponseInData(Boolean globalResponseInData) {
        this.globalResponseInData = globalResponseInData;
    }

    public Class getGlobalData() {
        return globalData == null ? Result.class : globalData;
    }

    public void setGlobalData(Class globalData) {
        this.globalData = globalData;
    }

    public String getGroupName() {
        return StrUtil.isEmpty(groupName) ? "default" : groupName ;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ContactProperty getContact() {
        return contact;
    }

    public void setContact(ContactProperty contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return "DocsProperty{" +
                "enabled=" + enabled +
                ", groupName='" + groupName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", globalResponseInData=" + globalResponseInData +
                ", globalData=" + globalData +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", version='" + version + '\'' +
                ", contact=" + contact +
                ", schemes=" + schemes +
                '}';
    }
}
