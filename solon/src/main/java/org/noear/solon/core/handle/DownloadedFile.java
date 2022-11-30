package org.noear.solon.core.handle;

import org.noear.solon.Utils;

import java.io.*;

/**
 * 下载的文件模型
 *
 * @author noear
 * @since 1.5
 */
public class DownloadedFile {
    /**
     * 内容类型（有些地方会动态构建，所以不能只读）
     *
     * @deprecated 1.11
     */
    @Deprecated
    public String contentType;
    /**
     * 内容流
     *
     * @deprecated 1.11
     */
    @Deprecated
    public InputStream content;
    /**
     * 文件名（带扩展名，例：demo.jpg）
     *
     * @deprecated 1.11
     */
    @Deprecated
    public String name;


    /**
     * 内容类型（有些地方会动态构建，所以不能只读）
     * */
    public InputStream getContent() {
        return content;
    }

    /**
     * 内容流
     * */
    public String getContentType() {
        return contentType;
    }

    /**
     * 文件名（带扩展名，例：demo.jpg）
     * */
    public String getName() {
        return name;
    }

    public DownloadedFile(){

    }

    /**
     * 基于下载输出的构建函数
     *
     * @param contentType 内容类型
     * @param content 内容流
     * @param name 文件名
     * */
    public DownloadedFile(String contentType, InputStream content, String name) {
        this.contentType = contentType;
        this.content = content;
        this.name = name;
    }

    public DownloadedFile(String contentType, byte[] content, String name) {
        this.contentType = contentType;
        this.content = new ByteArrayInputStream(content);
        this.name = name;
    }

    /**
     * 将内容流迁移到..
     *
     * @param file 文件
     * */
    public void transferTo(File file) throws IOException {
        try (FileOutputStream stream = new FileOutputStream(file)) {
            Utils.transferTo(content, stream);
        }
    }

    /**
     * 将内容流迁移到..
     *
     * @param stream 输出流
     * */
    public void transferTo(OutputStream stream) throws IOException {
        Utils.transferTo(content, stream);
    }
}
