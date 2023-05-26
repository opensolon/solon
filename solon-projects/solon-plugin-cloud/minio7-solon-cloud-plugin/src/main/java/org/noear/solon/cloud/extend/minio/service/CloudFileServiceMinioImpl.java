package org.noear.solon.cloud.extend.minio.service;

import io.minio.*;
import io.minio.http.Method;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.exception.CloudFileException;
import org.noear.solon.cloud.model.Media;
import org.noear.solon.cloud.service.CloudFileService;
import org.noear.solon.core.handle.Result;

import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 云端文件服务（minio）
 *
 * @author iYarnFog
 * @since 1.5
 */
public class CloudFileServiceMinioImpl implements CloudFileService {
    private final String bucketDef;

    private final String endpoint;
    private final String regionId;
    private final String accessKey;
    private final String secretKey;

    private final MinioClient client;

    public MinioClient getClient() {
        return client;
    }

    public CloudFileServiceMinioImpl(CloudProps cloudProps) {
        this(
                cloudProps.getFileEndpoint(),
                cloudProps.getFileRegionId(),
                cloudProps.getFileBucket(),
                cloudProps.getFileAccessKey(),
                cloudProps.getFileSecretKey()
        );
    }

    public CloudFileServiceMinioImpl(String endpoint, String regionId, String bucket, String accessKey, String secretKey) {
        this.endpoint = endpoint;
        this.regionId = regionId;

        this.bucketDef = bucket;

        this.accessKey = accessKey;
        this.secretKey = secretKey;

        this.client = MinioClient.builder()
                .endpoint(this.endpoint)
                .region(this.regionId)
                .credentials(this.accessKey, this.secretKey)
                .build();
    }

    @Override
    public boolean exists(String bucket, String key) throws CloudFileException {
        if (Utils.isEmpty(bucket)) {
            bucket = bucketDef;
        }

        try {
            ObjectStat stat = client.statObject(StatObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .build());

            return stat != null && stat.length() > 0;
        } catch (Exception exception) {
            throw new CloudFileException(exception);
        }
    }

    @Override
    public String getTempUrl(String bucket, String key, Date expiration) throws CloudFileException, UnsupportedOperationException {

        long seconds = (expiration.getTime() - System.currentTimeMillis()) / 1000L;

        try {
            String url = client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .expiry((int) seconds)
                    .method(Method.GET)
                    .build());

            return url;
        } catch (Exception e) {
            throw new CloudFileException(e);
        }
    }

    @Override
    public Media get(String bucket, String key) throws CloudFileException {
        if (Utils.isEmpty(bucket)) {
            bucket = bucketDef;
        }

        try {
            InputStream obj = client.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .build());

            return new Media(obj);
        } catch (Exception exception) {
            throw new CloudFileException(exception);
        }
    }

    @Override
    public Result<?> put(String bucket, String key, Media media) throws CloudFileException {
        if (Utils.isEmpty(bucket)) {
            bucket = bucketDef;
        }

        String streamMime = media.contentType();
        if (Utils.isEmpty(streamMime)) {
            streamMime = "text/plain; charset=utf-8";
        }

        try {
            ObjectWriteResponse response = this.client.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .stream(media.body(), media.contentSize(), -1)
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
            bucket = bucketDef;
        }

        try {
            this.client.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .build());
            return Result.succeed();
        } catch (Exception exception) {
            throw new CloudFileException(exception);
        }
    }

}
