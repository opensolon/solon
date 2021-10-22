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
     */
    public String contentType;
    /**
     * 内容流
     */
    public InputStream content;
    /**
     * 文件名（带扩展名，例：demo.jpg）
     */
    public String name;

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
