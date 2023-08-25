package org.noear.solon.core.handle;

import org.noear.solon.Utils;
import org.noear.solon.core.util.IoUtil;

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
    private String contentType;
    /**
     * 内容大小
     */
    private long contentSize;
    /**
     * 内容流
     */
    private InputStream content;
    /**
     * 文件名（带扩展名，例：demo.jpg）
     */
    private String name;
    /**
     * 是否附件（即下载模式）
     */
    private boolean attachment = true;

    /**
     * 是否附件
     */
    public boolean isAttachment() {
        return attachment;
    }

    /**
     * 作为附件
     * */
    public DownloadedFile asAttachment(boolean attachment) {
        this.attachment = attachment;
        return this;
    }

    /**
     * 内容流
     */
    public String getContentType() {
        return contentType;
    }


    /**
     * 内容大小
     */
    public long getContentSize() {
        if (contentSize > 0) {
            return contentSize;
        } else {
            try {
                return content.available();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 内容类型（有些地方会动态构建，所以不能只读）
     */
    public InputStream getContent() {
        return content;
    }

    /**
     * 文件名（带扩展名，例：demo.jpg）
     */
    public String getName() {
        return name;
    }

    public DownloadedFile() {

    }

    /**
     * 基于下载输出的构建函数
     *
     * @param contentType 内容类型
     * @param content     内容流
     * @param name        文件名
     */
    public DownloadedFile(String contentType, long contentSize, InputStream content, String name) {
        this.contentType = contentType;
        this.contentSize = contentSize;
        this.content = content;
        this.name = name;
    }

    public DownloadedFile(String contentType, InputStream content, String name) {
        this(contentType, 0, content, name);
    }

    public DownloadedFile(String contentType, byte[] content, String name) {
        this.contentType = contentType;
        this.contentSize = content.length;
        this.content = new ByteArrayInputStream(content);
        this.name = name;
    }

    public DownloadedFile(File file) throws FileNotFoundException {
        this.contentType = Utils.mime(file.getName());
        this.contentSize = 0;
        this.content = new FileInputStream(file);
        this.name = file.getName();
    }

    /**
     * 将内容流迁移到..
     *
     * @param file 文件
     */
    public void transferTo(File file) throws IOException {
        try (FileOutputStream stream = new FileOutputStream(file)) {
            IoUtil.transferTo(content, stream);
        }
    }

    /**
     * 将内容流迁移到..
     *
     * @param stream 输出流
     */
    public void transferTo(OutputStream stream) throws IOException {
        IoUtil.transferTo(content, stream);
    }
}
