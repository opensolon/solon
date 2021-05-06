package org.noear.solon.core.handle;

import java.io.InputStream;

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
     * 内容类型
     * */
    private String contentType;
    /**
     * 内容在小
     * */
    private long contentSize;
    /**
     * 内容流
     * */
    private InputStream content;
    /**
     * 文件名（带扩展名）
     * */
    private String name;
    /**
     * 扩展名
     * */
    private String extension;

    public UploadedFile(String contentType, long contentSize, InputStream content, String name, String extension){
        this.contentType = contentType;
        this.contentSize = contentSize;
        this.content = content;
        this.name = name;
        this.extension = extension;
    }

    public String contentType(){
        return contentType;
    }

    public long contentSize(){
        return contentSize;
    }

    public InputStream content(){
        return content;
    }

    public String name(){
        return name;
    }

    public String extension(){
        return extension;
    }

}
