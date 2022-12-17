# 配置示例

```yaml
solon.cloud.file.s3.file:
  bucket: 'huawei' 
  buckets:
    demo: #所用存储桶，必须都先配置好
      endpoint: 'https://obs.cn-southwest-2.myhuaweicloud.com' #通过协议，表达是否用 https?
      regionId: ''
      accessKey: 'xxxx'
      secretKey: 'xxx'
    demo2:
      endpoint: 'D:/img' # 或 '/data/sss/files'
      
```

# 实现本地文件上传,下载,删除简单案例

```java
package org.noear.solon.cloud.extend.file.s3.controller;


import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.noear.snack.core.utils.DateUtil;
import org.noear.snack.core.utils.StringUtil;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;
import org.noear.solon.cloud.extend.file.s3.UploadResult;
import org.noear.solon.cloud.extend.file.s3.core.OssClient;
import org.noear.solon.cloud.extend.file.s3.factory.OssFactory;
import org.noear.solon.cloud.extend.file.s3.properties.OssProperties;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.DownloadedFile;
import org.noear.solon.core.handle.Result;
import org.noear.solon.core.handle.UploadedFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.Date;

/**
 * 文件管理
 *
 * @author 等風來再離開
 * @date 2022/10/18 10:06
 **/

@Controller
@Mapping("/file")
public class FileController {


    /**
     * 文件下载
     *
     * @param context   上下文
     * @param configKey 配置key
     * @return
     */
    @SneakyThrows
    @Mapping(value = "/{configKey}/**")
    public DownloadedFile index(Context context, String configKey) {
        assert StringUtil.isEmpty(configKey);
        String local_prefix = Solon.cfg().get("solon.cloud.file.s3.file.local-prefix");
        String filePath = context.path().replace("/" + local_prefix, "").replace("/" + configKey, "");
        OssClient instance = OssFactory.instance(configKey);
        OssProperties ossProperties = instance.getOssProperties();
        String path = ossProperties.getLocalFilePath() + "/" + filePath;
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException(file.getName() + "文件不存在");
        }
        return new DownloadedFile(null, Files.newInputStream(file.toPath()), file.getName());
    }


    /**
     * 上传文件
     *
     * @param uploadFile 文件
     * @param path       文件路径
     * @param md5        文件MD5
     * @return
     */
    @SneakyThrows
    @Post
    @Mapping(value = "/upload")
    public Result upload(UploadedFile uploadFile, String path, String md5) {
        Result ret;
        if (uploadFile != null) {
            OssClient storage = OssFactory.instance();
            String originallyName = uploadFile.name;
            if (!StringUtil.isEmpty(md5)) {
                String md5Hex = DigestUtils.md5Hex(uploadFile.content);
                if (!md5Hex.equals(md5)) {
                    return Result.failure("文件被篡改，上传失败");
                }
            }
            boolean useOriginalName = false;
            if (StringUtil.isEmpty(path)) {
                path = DateUtil.format(new Date(), "yyyy/MM/dd") + "/" + originallyName;
            } else {
                useOriginalName = true;
            }
            //获取最后一个.的位置
            int lastIndexOf = uploadFile.name.lastIndexOf(".");
            //获取文件的后缀名 .jpg
            String suffix = uploadFile.name.substring(lastIndexOf);
            UploadResult uploadResult = storage.uploadSuffix(uploadFile.content, suffix, uploadFile.contentType, path, useOriginalName);
            uploadResult.setOriginalName(uploadFile.name);
            ret = Result.succeed(uploadResult, "文件[" + uploadFile.name + "][" + uploadFile.contentSize + "字节]上传成功");
        } else {
            ret = Result.failure("请选择文件后再操作");
        }
        return ret;
    }


    /**
     * 删除文件
     *
     * @param url       url
     * @param configKey 配置key
     * @return
     */
    @Post
    @Mapping(value = "/deleteFile")
    public Result deleteFile(String url, String configKey) {
        assert StringUtil.isEmpty(url);
        assert StringUtil.isEmpty(configKey);
        OssClient storage = OssFactory.instance(configKey);
        storage.delete(url);
        return Result.succeed("删除文件成功");
    }


}


```

