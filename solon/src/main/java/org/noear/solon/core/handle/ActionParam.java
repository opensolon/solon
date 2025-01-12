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
package org.noear.solon.core.handle;

/**
 * 动作参数
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