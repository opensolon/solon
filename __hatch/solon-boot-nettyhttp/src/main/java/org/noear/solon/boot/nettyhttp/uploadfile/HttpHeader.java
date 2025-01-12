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
package org.noear.solon.boot.nettyhttp.uploadfile;

public class HttpHeader {
    private final String name;
    private final String value;

    public HttpHeader(String name, String value) {
        this.name = name.trim();
        this.value = value.trim();
        // RFC2616#14.23 - header can have an empty value
        if (this.name.length() == 0) { // name cannot be empty
            throw new IllegalArgumentException("name cannot be empty");
        }
    }

    public String getName() {
        return name;
    }


    public String getValue() {
        return value;
    }
}
