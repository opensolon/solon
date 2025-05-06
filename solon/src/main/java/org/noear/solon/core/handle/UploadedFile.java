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

import org.noear.solon.Utils;
import org.noear.solon.core.util.IoUtil;

import java.io.*;

/**
 * 上传文件模型（例：通过http上传的文件）
 *
 * <pre>{@code
 * @Controller
 * public class DemoController{
 *     @Mapping("/update/")
 *     public String update(UploadedFile file){
 *         return "我收到文件：" + file.name;
 *     }
 * }
 * }</pre>
 *
 * @author noear
 * @since 1.0
 * @since 3.2
 * @update noear 20210506 添加字段访问控制
 * */
public class UploadedFile extends FileBase {
    /**
     * 删除动作
     */
    private Closeable deleteAction;

    /**
     * 扩展名（例：jpg）
     */
    private String extension;

    /**
     * 扩展名（例：jpg）
     */
    public String getExtension() {
        return extension;
    }

    /**
     * 内容流
     */
    public InputStream getContent() {
        return content;
    }

    /**
     * 内容字节形式
     *
     * @since 2.8
     */
    public byte[] getContentAsBytes() throws IOException {
        if (content == null) {
            //如果为空，则为空字节数组
            return new byte[0];
        }

        return IoUtil.transferToBytes(content);
    }

    /**
     * 内容大小
     */
    public long getContentSize() {
        if (contentSize > 0) {
            return contentSize;
        } else {
            try {
                if (content == null) {
                    return 0;
                } else {
                    return content.available();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * 上传文件
     */
    public UploadedFile() {
        super();
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @throws FileNotFoundException
     */
    public UploadedFile(File file) throws FileNotFoundException {
        this(file, file.getName());
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @param name 名字
     * @throws FileNotFoundException
     * @since 3.2
     */
    public UploadedFile(File file, String name) throws FileNotFoundException {
        this(file, name, Utils.mime(file.getName()));
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @param name 名字
     * @throws FileNotFoundException
     * @since 3.2
     */
    public UploadedFile(File file, String name, String contentType) throws FileNotFoundException {
        super(contentType, file.length(), new FileInputStream(file), name);
    }

    /**
     * 上传文件
     *
     * @param contentType 内容类型
     * @param content     内容流
     * @param name        文件名
     */
    public UploadedFile(String contentType, InputStream content, String name) {
        super(contentType, 0, content, name);
    }

    /**
     * 基于上传输入的构建函数
     *
     * @param contentType 内容类型
     * @param contentSize 内容大小
     * @param content     内容流
     * @param name        文件名
     * @param extension   文件后缀名
     */
    public UploadedFile(String contentType, long contentSize, InputStream content, String name, String extension) {
        this(null, contentType, contentSize, content, name, extension);
    }

    /**
     * 上传文件
     *
     * @param contentType 内容类型
     * @param contentSize 内容大小
     * @param content     内容流
     * @param name        文件名
     * @param extension   文件后缀名
     */
    public UploadedFile(Closeable deleteAction, String contentType, long contentSize, InputStream content, String name, String extension) {
        super(contentType, contentSize, content, name);
        this.extension = extension;
        this.deleteAction = deleteAction;
    }

    /**
     * 删除临时文件
     */
    public void delete() throws IOException {
        if (deleteAction != null) {
            deleteAction.close();
        }
    }

    /**
     * 是否为空
     */
    public boolean isEmpty() throws IOException {
        return getContentSize() == 0L;
    }

    /**
     * 将内容流迁移到目标文件
     *
     * @param file 目标文件
     */
    public void transferTo(File file) throws IOException {
        try (FileOutputStream stream = new FileOutputStream(file)) {
            IoUtil.transferTo(getContent(), stream);
        }
    }

    /**
     * 将内容流迁移到目标输出流
     *
     * @param stream 目标输出流
     */
    public void transferTo(OutputStream stream) throws IOException {
        IoUtil.transferTo(getContent(), stream);
    }
}
