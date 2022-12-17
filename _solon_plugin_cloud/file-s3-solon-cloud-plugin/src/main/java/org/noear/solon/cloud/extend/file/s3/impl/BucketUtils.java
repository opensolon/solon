package org.noear.solon.cloud.extend.file.s3.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CreateBucketRequest;

/**
 * 存储桶工具类
 *
 * @author 等風來再離開
 * @author noear
 * @since 1.11
 */
public class BucketUtils {
    public static final String[] CLOUD_SERVICE = new String[]{"aliyun", "qcloud", "qiniu", "huawei"};

    /**
     * 检查字符串包含情况
     * */
    public static boolean containsAny(String str, String... str2) {
        for (String s2 : str2) {
            if (str.contains(s2)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 初始化存储桶
     */
    public static void initBucket(AmazonS3 client, String bucketName) {
        if (client.doesBucketExistV2(bucketName)) {
            return;
        }

        String bucketPolicy = buildBucketPolicy(bucketName, PolicyType.READ);

        CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
        createBucketRequest.setCannedAcl(CannedAccessControlList.PublicRead);
        client.createBucket(createBucketRequest);
        client.setBucketPolicy(bucketName, bucketPolicy);
    }

    /**
     * 构建策略信息
     */
    private static String buildBucketPolicy(String bucketName, PolicyType policyType) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n\"Statement\": [\n{\n\"Action\": [\n");
        if (policyType == PolicyType.WRITE) {
            builder.append("\"s3:GetBucketLocation\",\n\"s3:ListBucketMultipartUploads\"\n");
        } else if (policyType == PolicyType.READ_WRITE) {
            builder.append("\"s3:GetBucketLocation\",\n\"s3:ListBucket\",\n\"s3:ListBucketMultipartUploads\"\n");
        } else {
            builder.append("\"s3:GetBucketLocation\"\n");
        }
        builder.append("],\n\"Effect\": \"Allow\",\n\"Principal\": \"*\",\n\"Resource\": \"arn:aws:s3:::");
        builder.append(bucketName);
        builder.append("\"\n},\n");
        if (policyType == PolicyType.READ) {
            builder.append("{\n\"Action\": [\n\"s3:ListBucket\"\n],\n\"Effect\": \"Deny\",\n\"Principal\": \"*\",\n\"Resource\": \"arn:aws:s3:::");
            builder.append(bucketName);
            builder.append("\"\n},\n");
        }
        builder.append("{\n\"Action\": ");
        switch (policyType) {
            case WRITE:
                builder.append("[\n\"s3:AbortMultipartUpload\",\n\"s3:DeleteObject\",\n\"s3:ListMultipartUploadParts\",\n\"s3:PutObject\"\n],\n");
                break;
            case READ_WRITE:
                builder.append("[\n\"s3:AbortMultipartUpload\",\n\"s3:DeleteObject\",\n\"s3:GetObject\",\n\"s3:ListMultipartUploadParts\",\n\"s3:PutObject\"\n],\n");
                break;
            default:
                builder.append("\"s3:GetObject\",\n");
                break;
        }
        builder.append("\"Effect\": \"Allow\",\n\"Principal\": \"*\",\n\"Resource\": \"arn:aws:s3:::");
        builder.append(bucketName);
        builder.append("/*\"\n}\n],\n\"Version\": \"2012-10-17\"\n}\n");
        return builder.toString();
    }
}
