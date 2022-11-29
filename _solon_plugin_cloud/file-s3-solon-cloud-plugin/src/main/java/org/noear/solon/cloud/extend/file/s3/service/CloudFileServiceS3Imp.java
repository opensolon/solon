package org.noear.solon.cloud.extend.file.s3.service;

import lombok.SneakyThrows;
import org.noear.snack.ONode;
import org.noear.snack.core.utils.StringUtil;
import org.noear.solon.Solon;
import org.noear.solon.cloud.exception.CloudFileException;
import org.noear.solon.cloud.extend.file.s3.UploadResult;
import org.noear.solon.cloud.extend.file.s3.constant.OssConstant;
import org.noear.solon.cloud.extend.file.s3.core.OssClient;
import org.noear.solon.cloud.extend.file.s3.exception.OssException;
import org.noear.solon.cloud.extend.file.s3.factory.OssFactory;
import org.noear.solon.cloud.extend.file.s3.properties.OssProperties;
import org.noear.solon.cloud.model.Media;
import org.noear.solon.cloud.service.CloudFileService;
import org.noear.solon.core.handle.Result;
import org.noear.solon.core.util.PrintUtil;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

/**
 * @author noear
 * @since 1.11
 */
public class CloudFileServiceS3Imp implements CloudFileService {

    public CloudFileServiceS3Imp() {
        List<Map> groupedProp = Solon.cfg().getBean("solon.cloud.file.s3.file.configs", List.class);
        if (!groupedProp.isEmpty()) {
            String platform = Solon.cfg().get("solon.cloud.file.s3.file.default-platform");
            if (StringUtil.isEmpty(platform)) {
                throw new OssException("default platform is not null");
            }
            groupedProp.forEach((map) -> {
                OssProperties ossProperties = ONode.deserialize(ONode.stringify(map), OssProperties.class);
                if (ossProperties.getEnable().equalsIgnoreCase(OssConstant.IS_HTTPS)) {
                    PrintUtil.info("s3 file init:" + ossProperties.getConfigKey());
                    Solon.cfg().put(OssConstant.CONFIG + ossProperties.getConfigKey(), ossProperties);
                    OssFactory.addClient(ossProperties.getConfigKey(), ossProperties);
                }
            });
        }
    }

    public CloudFileServiceS3Imp(String configKey, OssProperties ossProperties) {
        OssFactory.addClient(configKey, ossProperties);
    }

    @SneakyThrows
    @Override
    public Media get(String bucket, String key) throws CloudFileException {
        if (StringUtil.isEmpty(bucket)) {
            bucket = Solon.cfg().get("solon.cloud.file.s3.file.default-platform");
        }
        OssClient instance = OssFactory.instance(bucket);
        if (instance.isLocal()) {
            key = key.replace(instance.getUrl() + "/", "");
            String filePath = instance.getOssProperties().getLocalFilePath() + "/" + key;
            File file = new File(filePath);
            return new Media(Files.newInputStream(file.toPath()));
        } else {
            InputStream inputStream = instance.getS3Object(key).getObjectContent();
            return new Media(inputStream, key);
        }
    }

    @Override
    public Result put(String bucket, String key, Media media) throws CloudFileException {
        if (StringUtil.isEmpty(bucket)) {
            bucket = Solon.cfg().get("solon.cloud.file.s3.file.default-platform");
        }
        UploadResult upload = OssFactory.instance(bucket).upload(media.body(), key, media.contentType());
        return Result.succeed(upload);
    }

    @Override
    public Result delete(String bucket, String key) throws CloudFileException {
        if (StringUtil.isEmpty(bucket)) {
            bucket = Solon.cfg().get("solon.cloud.file.s3.file.default-platform");
        }
        OssFactory.instance(bucket).delete(key);
        return Result.succeed();
    }
}
