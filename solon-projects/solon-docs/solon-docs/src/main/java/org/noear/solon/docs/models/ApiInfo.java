/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.docs.models;

import java.io.Serializable;

/**
 * 接口信息
 *
 * @author noear
 * @since 2.2
 */
public class ApiInfo implements Serializable {
    private String description;
    private String version;
    private String title;
    private String termsOfService;
    private ApiContact contact;
    private ApiLicense license;

    public ApiInfo(){
        //用于反序列化
    }

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
