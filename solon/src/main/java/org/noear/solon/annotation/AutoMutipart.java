package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 允许多分片参数
 *
 * 一般附加在动作上
 *
 * <pre><code>
 * //1.默认禁掉
 * server.request.autoMultipart: false
 *
 * //2.按需开启
 * public class DemoController{
 *     @AutoMultipart
 *     @Mapping("/upload")
 *     public void upload(String userName, UploadedFile file){
 *
 *     }
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutoMutipart {
}
