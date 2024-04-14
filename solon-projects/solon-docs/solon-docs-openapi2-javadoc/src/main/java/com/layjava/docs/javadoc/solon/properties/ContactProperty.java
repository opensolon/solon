package com.layjava.docs.javadoc.solon.properties;

/**
 * 联系人/作者信息
 *
 * @author chengliang
 * @since 2024/04/11
 */
public class ContactProperty {
    /**
     * 姓名
     */
    private String name;
    /**
     * 电子邮件
     */
    private String email;
    /**
     * url
     */
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ContactProperty{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

}
