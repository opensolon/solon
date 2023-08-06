package org.noear.solon.docs.models;

/**
 * 接口信息
 *
 * @author noear
 * @since 2.2
 */
public class ApiInfo {
    private String description;
    private String version;
    private String title;
    private String termsOfService;
    private ApiContact contact;
    private ApiLicense license;

    public String description() {
        return description;
    }

    public ApiInfo description(String description) {
        this.description = description;
        return this;
    }

    public String version() {
        return version;
    }

    public ApiInfo version(String version) {
        this.version = version;
        return this;
    }

    public String title() {
        return title;
    }

    public ApiInfo title(String title) {
        this.title = title;
        return this;
    }

    public String termsOfService() {
        return termsOfService;
    }

    public ApiInfo termsOfService(String url) {
        this.termsOfService = url;
        return this;
    }

    public ApiContact contact() {
        return contact;
    }

    public ApiInfo contact(ApiContact contact) {
        this.contact = contact;
        return this;
    }

    public ApiInfo contact(String name, String url, String email) {
        this.contact = new ApiContact().name(name).url(url).email(email);
        return this;
    }

    public ApiLicense license() {
        return license;
    }

    public ApiInfo license(ApiLicense license) {
        this.license = license;
        return this;
    }

    public ApiInfo license(String name, String url) {
        this.license = new ApiLicense().name(name).url(url);
        return this;
    }
}
