package org.noear.solon.cloud.extend.minio.service;

import io.minio.*;
import org.noear.solon.Utils;
import org.noear.solon.cloud.exception.CloudFileException;
import org.noear.solon.cloud.extend.minio.MinioProps;
import org.noear.solon.cloud.model.Media;
import org.noear.solon.cloud.service.CloudFileService;
import org.noear.solon.core.handle.Result;

import java.util.Properties;

/**
 * 云端文件服务（minio）
 *
 * @author iYarnFog
 * @since 1.5
 */
public class CloudFileServiceMinioImp implements CloudFileService {

    private static CloudFileServiceMinioImp instance;

    public static synchronized CloudFileServiceMinioImp getInstance() {
        if (instance == null) {
            instance = new CloudFileServiceMinioImp();
        }
        return instance;
    }

    protected final String endpoint;
    protected final String regionId;
    protected final String bucket;
    protected final String accessKey;
    protected final String secretKey;
    protected final MinioClient minioClient;

    private CloudFileServiceMinioImp() {
        this(
                MinioProps.INSTANCE.getFileEndpoint(),
                MinioProps.INSTANCE.getFileRegionId(),
                MinioProps.INSTANCE.getFileBucket(),
                MinioProps.INSTANCE.getFileAccessKey(),
                MinioProps.INSTANCE.getFileSecretKey()
        );
    }

    public CloudFileServiceMinioImp(Properties properties) {
        this(
                properties.getProperty("endpoint"),
                properties.getProperty("regionId"),
                properties.getProperty("bucket"),
                properties.getProperty("accessKey"),
                properties.getProperty("secretKey")
        );
    }

    public CloudFileServiceMinioImp(String endpoint, String regionId, String bucket, String accessKey, String secretKey) {
        this.endpoint = endpoint;
        this.regionId = regionId;
        this.bucket = bucket;
        this.accessKey = accessKey;
        this.secretKey = secretKey;

        this.minioClient = MinioClient.builder()
                .endpoint(this.endpoint)
                .region(this.regionId)
                .credentials(this.accessKey, this.secretKey)
                .build();
    }

    @Override
    public Media get(String bucket, String key) throws CloudFileException {
        if (Utils.isEmpty(bucket)) {
            bucket = this.bucket;
        }

        try {
            GetObjectResponse obj = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .build());

            return new Media(obj, obj.headers().get("Content-Type"));
        } catch (Exception exception) {
            throw new CloudFileException(exception);
        }
    }

    @Override
    public Result<?> put(String bucket, String key, Media media) throws CloudFileException {
        if (Utils.isEmpty(bucket)) {
            bucket = this.bucket;
        }

        String streamMime = media.contentType();
        if (Utils.isEmpty(streamMime)) {
            streamMime = "text/plain; charset=utf-8";
        }

        try {
            ObjectWriteResponse response = this.minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .stream(media.body(), media.body().available(), -1)
                    .contentType(streamMime)
                    .build());

            return Result.succeed(response);
        } catch (Exception exception) {
            throw new CloudFileException(exception);
        }
    }

    @Override
    public Result<?> delete(String bucket, String key) throws CloudFileException {
        if (Utils.isEmpty(bucket)) {
            bucket = this.bucket;
        }

        try {
            this.minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .build());
            return Result.succeed();
        } catch (Exception exception) {
            throw new CloudFileException(exception);
        }
    }

    public MinioClient getMinio() {
        return this.minioClient;
    }
}
