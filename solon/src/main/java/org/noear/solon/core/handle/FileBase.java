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

import java.io.*;

/**
 * 文件基类
 *
 * @author noear
 * @since 2.8
 */
public abstract class FileBase {
    /**
     * 内容流
     */
    protected InputStream content;
    /**
     * 内容类型（有些地方会动态构建，所以不能只读）
     */
    protected String contentType;
    /**
     * 内容大小
     */
    protected long contentSize;
    /**
     * 文件名（带扩展名，例：demo.jpg）
     */
    protected String name;


    /**
     * 内容类型
     */
    public String getContentType() {
        return contentType;
    }


    /**
     * 文件名（带扩展名，例：demo.jpg）
     */
    public String getName() {
        return name;
    }

    public FileBase() {

    }

    /**
     * 构造函数
     *
     * @param contentType 内容类型
     * @param contentSize 内容大小
     * @param content     内容流
     * @param name        文件名
     */
    public FileBase(String contentType, long contentSize, InputStream content, String name) {
        this.contentType = contentType;
        this.contentSize = contentSize;
        this.content = content;
        this.name = name;
    }
}