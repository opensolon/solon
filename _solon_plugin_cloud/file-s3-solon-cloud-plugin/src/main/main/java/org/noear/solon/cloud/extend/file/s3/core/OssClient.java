package org.noear.solon.cloud.extend.file.s3.core;

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
import lombok.SneakyThrows;
import org.noear.snack.core.utils.DateUtil;
import org.noear.snack.core.utils.StringUtil;
import org.noear.solon.Solon;
import org.noear.solon.cloud.extend.file.s3.UploadResult;
import org.noear.solon.cloud.extend.file.s3.constant.OssConstant;
import org.noear.solon.cloud.extend.file.s3.enumd.PolicyType;
import org.noear.solon.cloud.extend.file.s3.exception.OssException;
import org.noear.solon.cloud.extend.file.s3.properties.OssProperties;
import org.noear.solon.core.util.PrintUtil;

import java.io.*;
import java.util.Date;
import java.util.UUID;

/**
 * S3 存储协议 所有兼容S3协议的云厂商均支持
 * 阿里云 腾讯云 七牛云 minio
 *
 * @author 等風來再離開
 */
public class OssClient {

    private final String configKey;

    private final OssProperties properties;

    private final AmazonS3 client;

    public OssClient(String configKey, OssProperties ossProperties) {
        this.configKey = configKey;
        this.properties = ossProperties;
        try {

            if (!properties.getIsLocal().equals(OssConstant.IS_LOCAL)) {

                AwsClientBuilder.EndpointConfiguration endpointConfig = new AwsClientBuilder.EndpointConfiguration(properties.getEndpoint(), properties.getRegion());

                AWSCredentials credentials = new BasicAWSCredentials(properties.getAccessKey(), properties.getSecretKey());
                AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);
                ClientConfiguration clientConfig = new ClientConfiguration();
                if (OssConstant.IS_HTTPS.equals(properties.getIsHttps())) {
                    clientConfig.setProtocol(Protocol.HTTPS);
                } else {
                    clientConfig.setProtocol(Protocol.HTTP);
                }
                AmazonS3ClientBuilder build = AmazonS3Client.builder().withEndpointConfiguration(endpointConfig).withClientConfiguration(clientConfig).withCredentials(credentialsProvider).disableChunkedEncoding();
                if (!containsAny(properties.getEndpoint(), OssConstant.CLOUD_SERVICE)) {
                    // minio 使用https限制使用域名访问 需要此配置 站点填域名
                    build.enablePathStyleAccess();
                }
                this.client = build.build();

            } else {
                this.client = null;
            }
            createBucket();

        } catch (Exception e) {
            if (e instanceof OssException) {
                throw e;
            }
            throw new OssException("配置错误! 请检查系统配置:[" + e.getMessage() + "]");
        }
    }

    /**
     * 查找指定字符串是否包含指定字符串列表中的任意一个字符串
     *
     * @param str      指定字符串
     * @param testStrs 需要检查的字符串数组
     * @return 是否包含任意一个字符串
     */
    public static boolean containsAny(CharSequence str, CharSequence... testStrs) {
        return null != getContainsStr(str, testStrs);
    }

    /**
     * 查找指定字符串是否包含指定字符串列表中的任意一个字符串，如果包含返回找到的第一个字符串
     *
     * @param str      指定字符串
     * @param testStrs 需要检查的字符串数组
     * @return 被包含的第一个字符串
     */
    public static String getContainsStr(CharSequence str, CharSequence... testStrs) {
        if (null == str || testStrs == null || testStrs.length == 0) {
            return null;
        }
        for (CharSequence checkStr : testStrs) {
            if (str.toString().contains(checkStr)) {
                return checkStr.toString();
            }
        }
        return null;
    }

    private static String getPolicy(String bucketName, PolicyType policyType) {
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

    /**
     * 创建桶/本地创建文件夹
     */
    public void createBucket() {
        try {
            if (!properties.getIsLocal().equals(OssConstant.IS_LOCAL)) {
                String bucketName = properties.getBucketName();
                if (client.doesBucketExistV2(bucketName)) {
                    return;
                }
                CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
                createBucketRequest.setCannedAcl(CannedAccessControlList.PublicRead);
                client.createBucket(createBucketRequest);
                client.setBucketPolicy(bucketName, getPolicy(bucketName, PolicyType.READ));
            } else {
                String localFilePath = properties.getLocalFilePath();
                File file = new File(localFilePath);
                if (!file.exists()) {
                    boolean result = file.mkdirs();
                    PrintUtil.info(localFilePath + "创建目录:" + result);
                }
            }
        } catch (Exception e) {
            throw new OssException("创建Bucket失败, 请核对配置信息:[" + e.getMessage() + "]");
        }
    }

    public UploadResult upload(InputStream inputStream, String path, String contentType) {
        String configKey = this.getConfigKey();
        int lastIndexOf = path.lastIndexOf(".");
        //获取文件的后缀名 例如: .jpg
        String suffix = path.substring(lastIndexOf);
        try {
            if (properties.getIsLocal().equals(OssConstant.IS_LOCAL)) {
                String localFilePath = properties.getLocalFilePath();
                File file = new File(localFilePath + "/" + path);
                // 如果目录不存在则创建
                File parentPath = file.getCanonicalFile().getParentFile();
                if (!parentPath.exists()) {
                    parentPath.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(file);
                try {
                    byte[] b = new byte[1024];
                    int len;
                    while ((len = inputStream.read(b)) != -1) {
                        fos.write(b, 0, len);
                    }
                } finally {
                    if (null != inputStream) {
                        inputStream.close();
                    }
                    if (null != fos) {
                        fos.close();
                    }
                }

            } else {
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType(contentType);
                metadata.setContentLength(inputStream.available());
                PutObjectRequest putObjectRequest = new PutObjectRequest(properties.getBucketName(), path, inputStream, metadata);
                // 设置上传对象的 Acl 为公共读
                putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
                client.putObject(putObjectRequest);
            }
        } catch (Exception e) {
            throw new OssException("上传文件失败，请检查配置信息:[" + e.getMessage() + "]");
        }
        return UploadResult.builder().url(getUrl() + "/" + path).fileName(path).configKey(configKey).fileSuffix(suffix).endpoint(getUrl() + "/").build();
    }

    @SneakyThrows
    public void delete(String path) {
        path = path.replace(getUrl() + "/", "");
        if (properties.getIsLocal().equals(OssConstant.IS_LOCAL)) {
            String localFilePath = properties.getLocalFilePath() + "/" + path;
            File file = new File(localFilePath);
            if (file.exists()) {
                boolean delete = file.delete();
                PrintUtil.info(properties.getConfigKey() + "删除文件:" + delete);
            } else {
                PrintUtil.info(file.getAbsolutePath() + "文件不存在");
            }
        } else {
            try {
                client.deleteObject(properties.getBucketName(), path);
            } catch (Exception e) {
                throw new OssException("删除文件失败，请检查配置信息:[" + e.getMessage() + "]");
            }
        }
    }

    /**
     * 获取文件元数据
     *
     * @param path 完整文件路径
     */
    public ObjectMetadata getObjectMetadata(String path) {
        S3Object object = client.getObject(properties.getBucketName(), path);
        return object.getObjectMetadata();
    }

    /**
     * 获取S3对象
     *
     * @param path 完整文件路径
     * @return
     */
    public S3Object getS3Object(String path) {
        return client.getObject(properties.getBucketName(), path);
    }

    public String getUrl() {
        String domain = properties.getDomain();
        String endpoint = properties.getEndpoint();
        String header = OssConstant.IS_HTTPS.equals(properties.getIsHttps()) ? "https://" : "http://";
        // 云服务商直接返回
        if (containsAny(endpoint, OssConstant.CLOUD_SERVICE)) {
            if (!StringUtil.isEmpty(domain)) {
                return header + domain;
            }
            return header + properties.getBucketName() + "." + endpoint;
        }
        // 本地存储单独返回
        String local_prefix = Solon.cfg().get("solon.cloud.file.s3.file.local-prefix");
        if (properties.getIsLocal().equals(OssConstant.IS_LOCAL)) {
            if (!StringUtil.isEmpty(domain)) {
                return header + domain + "/" + local_prefix + "/" + this.getConfigKey();
            }
            return header + endpoint + "/" + local_prefix + "/" + this.getConfigKey();
        }

        // minio 单独处理
        if (!StringUtil.isEmpty(domain)) {
            return header + domain + "/" + properties.getBucketName();
        }


        return header + endpoint + "/" + properties.getBucketName();
    }

    /**
     * 获取文件路径
     *
     * @param prefix          文件前缀
     * @param suffix          文件后缀
     * @param filePath        文件路径
     * @param useOriginalName 是否使用原文件名
     * @return
     */
    public String getPath(String prefix, String suffix, String filePath, boolean useOriginalName) {

        // 文件路径
        String path;
        if (useOriginalName) {
            return filePath;
        } else {
            // 生成uuid
            String uuid = UUID.randomUUID().toString();
            path = DateUtil.format(new Date(), "yyyy/MM/dd") + "/" + uuid;
        }
        if (!StringUtil.isEmpty(prefix)) {
            path = prefix + "/" + path;
        }
        return path + suffix;
    }

    public String getConfigKey() {
        return configKey;
    }

    /**
     * 获取配置
     *
     * @return
     */
    public OssProperties getOssProperties() {
        return this.properties;
    }


    /**
     * 获取文件路径
     *
     * @param inputStream     输入流
     * @param fileSuffix      文件后缀
     * @param filePath        文件路径
     * @param useOriginalName 是否使用原文件名
     * @param contentType     contentType
     * @return
     */

    public UploadResult uploadSuffix(InputStream inputStream, String fileSuffix, String contentType, String filePath, boolean useOriginalName) {
        return upload(inputStream, getPath(properties.getPrefix(), fileSuffix, filePath, useOriginalName), contentType);
    }


    /**
     * @param file            文件
     * @param fileSuffix      文件后缀
     * @param contentType     contentType
     * @param filePath        自定义文件路径
     * @param useOriginalName 是否使用原始文件名
     * @return
     */
    public UploadResult uploadSuffix(File file, String fileSuffix, String contentType, String filePath, boolean useOriginalName) {
        return upload(file, getPath(properties.getPrefix(), fileSuffix, filePath, useOriginalName), contentType);
    }


    /**
     * 上传文件
     *
     * @param data        数据
     * @param path        文件存储路径
     * @param contentType contentType
     * @return
     */
    public UploadResult upload(byte[] data, String path, String contentType) {
        return upload(new ByteArrayInputStream(data), path, contentType);
    }


    /**
     * 上传文件
     *
     * @param file        文件
     * @param path        文件路径
     * @param contentType contentType
     * @return
     */
    @SneakyThrows
    public UploadResult upload(File file, String path, String contentType) {
        InputStream inputStream = new FileInputStream(file);
        if (StringUtil.isEmpty(path)) {
            path = DateUtil.format(new Date(), "yyyy/MM/dd") + "/" + file.getName();
        }
        return upload(inputStream, path, contentType);
    }


    /**
     * 是否是本地存储
     *
     * @return
     */
    public boolean isLocal() {
        return properties.getIsLocal().equals(OssConstant.IS_LOCAL);
    }

}
