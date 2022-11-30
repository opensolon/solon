package org.noear.solon.core.handle;


import java.io.*;

/**
 * 上传的文件模型（例：通过http上传的文件）
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
public class UploadedFile extends DownloadedFile{
    /**
     * 内容大小
     *
     * @deprecated 1.11
     */
    @Deprecated
    public long contentSize;
    /**
     * 扩展名（例：jpg）
     *
     * @deprecated 1.11
     */
    @Deprecated
    public String extension;


    /**
     * 内容大小
     * */
    public long getContentSize() {
        return contentSize;
    }

    /**
     * 扩展名（例：jpg）
     * */
    public String getExtension() {
        return extension;
    }

    public UploadedFile(){
        super();
    }

    /**
     * 基于下载输出的构建函数
     *
     * @param contentType 内容类型
     * @param content 内容流
     * @param name 文件名
     * */
    public UploadedFile(String contentType, InputStream content, String name) {
        super(contentType, content, name);
    }

    /**
     * 基于上传输入的构建函数
     *
     * @param contentType 内容类型
     * @param contentSize 内容大小
     * @param content 内容流
     * @param name 文件名
     * @param extension 文件后缀名
     * */
    public UploadedFile(String contentType, long contentSize, InputStream content, String name, String extension) {
        super(contentType, content, name);
        this.contentSize = contentSize;
        this.extension = extension;
    }

    /**
     * 是否为空
     * */
    public boolean isEmpty() {
        return contentSize == 0L;
    }

}
