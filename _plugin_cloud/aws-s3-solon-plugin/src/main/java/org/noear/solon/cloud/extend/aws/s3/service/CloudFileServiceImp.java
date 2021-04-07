package org.noear.solon.cloud.extend.aws.s3.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.StringInputStream;
import org.noear.solon.cloud.extend.aws.s3.S3Props;
import org.noear.solon.cloud.service.CloudFileService;
import org.noear.solon.core.handle.Result;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * 云端文件服务（aws s3）
 *
 * @author noear
 * @since 1.3
 */
public class CloudFileServiceImp implements CloudFileService {
    protected final String bucket;
    protected final String accessKey;
    protected final String secretKey;
    protected final String endpoint;
    protected final String regionId;

    protected final AmazonS3 client;
    protected final AccessControlList acls = new AccessControlList();

    public CloudFileServiceImp() {
        this(S3Props.instance.getFileEndpoint(),
                S3Props.instance.getFileRegionId(),
                S3Props.instance.getFileBucket(),
                S3Props.instance.getFileAccessKey(),
                S3Props.instance.getFileSecretKey());
    }

    public CloudFileServiceImp(String endpoint, String regionId, String bucket, String accessKey, String secretKey) {
        this.endpoint = endpoint;
        this.regionId = regionId;

        this.bucket = bucket;

        this.accessKey = accessKey;
        this.secretKey = secretKey;

        //初始化client
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        client = AmazonS3ClientBuilder.standard().withRegion(regionId).withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();

        //初始化权限控制
        acls.grantPermission(GroupGrantee.AllUsers, Permission.Read);
    }

    @Override
    public String getString(String key) throws Exception {
        return null;
    }

    @Override
    public Result putString(String key, String content) throws Exception {
        InputStream stream = new StringInputStream(content);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("text/plain");

        PutObjectRequest request = new PutObjectRequest(bucket, key, stream, metadata)
                .withAccessControlList(acls);

        PutObjectResult tmp = client.putObject(request);

        return Result.succeed(tmp);
    }

    @Override
    public Result putFile(String key, File file) throws Exception {
        PutObjectRequest request = new PutObjectRequest(bucket, key, file)
                .withAccessControlList(acls);

        PutObjectResult tmp = client.putObject(request);

        return Result.succeed(tmp);
    }
}
