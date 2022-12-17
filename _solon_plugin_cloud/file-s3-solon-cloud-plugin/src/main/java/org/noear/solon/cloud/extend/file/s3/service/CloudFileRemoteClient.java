package org.noear.solon.cloud.extend.file.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.noear.solon.Utils;
import org.noear.solon.cloud.exception.CloudFileException;
import org.noear.solon.cloud.extend.file.s3.impl.BucketUtils;
import org.noear.solon.cloud.model.Media;
import org.noear.solon.cloud.service.CloudFileService;
import org.noear.solon.core.Props;
import org.noear.solon.core.handle.Result;

/**
 * CloudFileService 的远程实现（基于 s3 协议）
 *
 * @author 等風來再離開
 * @since 1.11
 */
public class CloudFileRemoteClient implements CloudFileService {
    private final AmazonS3 client;

    public CloudFileRemoteClient(String bucketName, Props props) {
        this.client = BucketUtils.createClient(props);
    }


    @Override
    public Media get(String bucket, String key) throws CloudFileException {
        try {
            GetObjectRequest request = new GetObjectRequest(bucket, key);

            S3Object obj = client.getObject(request);

            return new Media(obj.getObjectContent(), obj.getObjectMetadata().getContentType());
        } catch (Exception ex) {
            throw new CloudFileException(ex);
        }
    }

    @Override
    public Result put(String bucket, String key, Media media) throws CloudFileException {
        String streamMime = media.contentType();
        if (Utils.isEmpty(streamMime)) {
            streamMime = "text/plain; charset=utf-8";
        }

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(streamMime);

            PutObjectRequest request = new PutObjectRequest(bucket, key, media.body(), metadata);
            request.setCannedAcl(CannedAccessControlList.PublicRead);

            PutObjectResult tmp = client.putObject(request);

            return Result.succeed(tmp);
        } catch (Exception ex) {
            throw new CloudFileException(ex);
        }
    }

    @Override
    public Result delete(String bucket, String key) throws CloudFileException {
        client.deleteObject(bucket, key);
        return Result.succeed();
    }
}
