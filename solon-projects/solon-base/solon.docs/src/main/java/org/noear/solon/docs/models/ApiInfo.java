package org.noear.solon.docs.models;

import io.swagger.models.Contact;
import io.swagger.models.License;

/**
 * @author noear
 * @since 2.3
 */
public class ApiInfo {
    private String description;
    private String version;
    private String title;
    private String termsOfService;
    private Contact contact;
    private License license;

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

    public Contact contact() {
        return contact;
    }

    public ApiInfo contact(Contact contact) {
        this.contact = contact;
        return this;
    }

    public ApiInfo contact(String name, String url, String email) {
        this.contact = new Contact().name(name).url(url).email(email);
        return this;
    }

    public License license() {
        return license;
    }

    public ApiInfo license(License license) {
        this.license = license;
        return this;
    }

    public ApiInfo license(String name, String url) {
        this.license = new License().name(name).url(url);
        return this;
    }
}
