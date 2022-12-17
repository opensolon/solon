package org.noear.solon.cloud.extend.file.s3.service;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.noear.solon.Utils;
import org.noear.solon.cloud.exception.CloudFileException;
import org.noear.solon.cloud.extend.file.s3.impl.BucketUtils;
import org.noear.solon.cloud.model.Media;
import org.noear.solon.cloud.service.CloudFileService;
import org.noear.solon.core.handle.Result;

import java.net.URI;
import java.util.Properties;

/**
 * CloudFileService 的 s3 实现
 *
 * @author 等風來再離開
 * @since 1.11
 */
public class CloudFileS3Client implements CloudFileService {
    private final AmazonS3 client;

    private final String endpoint;
    private final String regionId;

    private final String accessKey;
    private final String secretKey;

    public CloudFileS3Client(String bucketName, Properties properties) {
        String endpointStr = properties.getProperty("endpoint");
        URI endpointUri = URI.create(endpointStr);

        this.endpoint = endpointUri.getHost();
        this.regionId = properties.getProperty("regionId");

        this.accessKey = properties.getProperty("accessKey");
        this.secretKey = properties.getProperty("secretKey");


        AwsClientBuilder.EndpointConfiguration endpointConfig = new AwsClientBuilder.EndpointConfiguration(endpoint, regionId);

        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);
        ClientConfiguration clientConfig = new ClientConfiguration();

        if ("https".equals(endpointUri.getScheme())) {
            clientConfig.setProtocol(Protocol.HTTPS);
        } else {
            clientConfig.setProtocol(Protocol.HTTP);
        }

        AmazonS3ClientBuilder build = AmazonS3Client.builder().withEndpointConfiguration(endpointConfig).withClientConfiguration(clientConfig).withCredentials(credentialsProvider).disableChunkedEncoding();

        if (BucketUtils.containsAny(endpoint, BucketUtils.CLOUD_SERVICE)) {
            // minio 使用https限制使用域名访问 需要此配置 站点填域名
            build.enablePathStyleAccess();
        }

        this.client = build.build();

        BucketUtils.initBucket(client, bucketName);
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
