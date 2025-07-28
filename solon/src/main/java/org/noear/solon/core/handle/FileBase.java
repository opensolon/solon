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

import org.noear.solon.core.util.SupplierEx;

import java.io.*;
import java.util.Objects;

/**
 * 文件基类
 *
 * @author noear
 * @since 2.8
 */
public abstract class FileBase implements Closeable {
    /**
     * 内容流提供者
     */
    private SupplierEx<InputStream> contentSupplier;
    private InputStream content;
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
     * 文件名（带扩展名，例：demo.jpg）
     */
    public String getName() {
        return name;
    }

    /**
     * 内容类型
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * 内容流
     */
    public InputStream getContent() {
        try {
            if (content == null) {
                content = contentSupplier.get();
            }

            return content;
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void close() throws IOException {
        if (content != null) {
            content.close();
        }
    }

    public FileBase() {

    }

    /**
     * 构造函数
     *
     * @param contentType     内容类型
     * @param contentSize     内容大小
     * @param contentSupplier 内容提供者
     * @param name            文件名
     */
    public FileBase(String contentType, long contentSize, SupplierEx<InputStream> contentSupplier, String name) {
        this.contentType = contentType;
        this.contentSize = contentSize;
        this.contentSupplier = contentSupplier;
        this.name = name;

        Objects.requireNonNull(contentSupplier, "The content supplier cannot be null");
    }
}