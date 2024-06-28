package org.noear.solon.core.handle;

import java.io.*;

/**
 * 上传文件模型（例：通过http上传的文件）
 *
 * <pre><code>
 * @Controller
 * public class DemoController{
 *     @Mapping("/update/")
 *     public String update(UploadedFile file){
 *         return "我收到文件：" + file.name;
 *     }
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.0
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
     * 上传文件
     */
    public UploadedFile() {
        super();
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
}