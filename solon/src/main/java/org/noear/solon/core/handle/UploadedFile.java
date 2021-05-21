package org.noear.solon.core.handle;

import org.noear.solon.Utils;

import java.io.*;

/**
 * 通用上传文件模型（例：通过http上传的文件）
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
public class UploadedFile {
    /**
     * 内容类型（有些地方会动态构建，所以不能只读）
     */
    public String contentType;
    /**
     * 内容大小
     */
    public long contentSize;
    /**
     * 内容流
     */
    public InputStream content;
    /**
     * 文件名（带扩展名）
     */
    public String name;
    /**
     * 扩展名
     */
    public String extension;

    public UploadedFile(){

    }

    public UploadedFile(String contentType, long contentSize, InputStream content, String name, String extension) {
        this.contentType = contentType;
        this.contentSize = contentSize;
        this.content = content;
        this.name = name;
        this.extension = extension;
    }

    /**
     * 是否为空
     * */
    public boolean isEmpty() {
        return contentSize == 0L;
    }

    /**
     * 将内容流迁移到..
     *
     * @param file 文件
     * */
    public void transferTo(File file) throws IOException {
        Utils.transferTo(content, new FileOutputStream(file));
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
