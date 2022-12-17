package org.noear.solon.cloud.extend.aws.s3.service;

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
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.exception.CloudFileException;
import org.noear.solon.cloud.model.Media;
import org.noear.solon.cloud.service.CloudFileService;
import org.noear.solon.core.Props;
import org.noear.solon.core.handle.Result;

import java.net.URI;

/**
 * 云端文件服务（aws s3）
 *
 * @author noear
 * @since 1.3
 */
public class CloudFileServiceS3OfSdkImp implements CloudFileService {
    private final String bucketDef;

    private final AmazonS3 client;


    public CloudFileServiceS3OfSdkImp(CloudProps cloudProps) {
        this(
                cloudProps.getFileEndpoint(),
                cloudProps.getFileRegionId(),
                cloudProps.getFileBucket(),
                cloudProps.getFileAccessKey(),
                cloudProps.getFileSecretKey(),
                cloudProps.getProp()
        );
    }

    public CloudFileServiceS3OfSdkImp(String endpoint, String regionId, String bucket, String accessKey, String secretKey, Props props) {
        this.bucketDef = bucket;

        //初始化client
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);
        ClientConfiguration clientConfig = new ClientConfiguration();

        if (Utils.isEmpty(endpoint)) {
            clientConfig.setProtocol(Protocol.HTTPS);

            this.client = AmazonS3ClientBuilder.standard()
                    .withRegion(regionId)
                    .withClientConfiguration(clientConfig)
                    .withCredentials(credentialsProvider)
                    .build();
        } else {
            URI endpointUri = URI.create(endpoint);
            endpoint = endpointUri.getHost();

            AwsClientBuilder.EndpointConfiguration endpointConfig = new AwsClientBuilder
                    .EndpointConfiguration(endpoint, regionId);

            if ("http".equals(endpointUri.getScheme())) {
                clientConfig.setProtocol(Protocol.HTTP);
            } else {
                clientConfig.setProtocol(Protocol.HTTPS);
            }

            //开始构建
            AmazonS3ClientBuilder builder = AmazonS3Client.builder()
                    .withEndpointConfiguration(endpointConfig)
                    .withClientConfiguration(clientConfig)
                    .withCredentials(credentialsProvider);

            //注入配置
            if (props != null && props.size() > 0) {
                Utils.injectProperties(builder, props);
            }

            this.client = builder.build();
        }
    }

    @Override
    public Media get(String bucket, String key) throws CloudFileException {
        if (Utils.isEmpty(bucket)) {
            bucket = bucketDef;
        }

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
        if (Utils.isEmpty(bucket)) {
            bucket = bucketDef;
        }

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
        if (Utils.isEmpty(bucket)) {
            bucket = bucketDef;
        }

        client.deleteObject(bucket, key);

        return Result.succeed();
    }
}
