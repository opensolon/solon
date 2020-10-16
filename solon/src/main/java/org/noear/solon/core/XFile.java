package org.noear.solon.core;

import java.io.InputStream;

/**
 * 通用上传文件模型（例：通过http上传的文件）
 *
 * <pre><code>
 * @XController
 * public class DemoController{
 *     @XMapping("/update/")
 *     public String update(XFile file){
 *         return "我收到文件：" + file.name;
 *     }
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
public class XFile {
    /**内容类型*/
    public String contentType;
    /**内容流*/
    public InputStream content;
    /**文件名*/
    public String name;
    /**扩展名*/
    public String extension;
}
