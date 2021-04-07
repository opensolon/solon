package org.noear.solon.cloud.extend.aliyun.oss.service;

import org.noear.solon.Utils;
import org.noear.solon.cloud.exception.CloudFileException;
import org.noear.solon.cloud.extend.aliyun.oss.OssProps;
import org.noear.solon.cloud.service.CloudFileService;
import org.noear.solon.core.handle.Result;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 云端文件服务（aliyun oss）
 *
 * @author noear
 * @since 1.3
 */
public class CloudFileServiceOssImp implements CloudFileService {
    protected final String bucketDef;
    protected final String accessKey;
    protected final String secretKey;
    protected final String endpoint;
    protected final String regionId;

    protected String CHARSET_UTF8 = "utf8";
    protected String ALGORITHM = "HmacSHA1";

    public CloudFileServiceOssImp() {
        this(
                OssProps.instance.getFileEndpoint(),
                OssProps.instance.getFileRegionId(),
                OssProps.instance.getFileBucket(),
                OssProps.instance.getFileAccessKey(),
                OssProps.instance.getFileSecretKey()
        );
    }

    public CloudFileServiceOssImp(Properties pops) {
        this(
                pops.getProperty("endpoint"),
                pops.getProperty("regionId"),
                pops.getProperty("bucket"),
                pops.getProperty("accessKey"),
                pops.getProperty("secretKey")
        );
    }


    public CloudFileServiceOssImp(String endpoint, String regionId, String bucket, String accessKey, String secretKey) {
        this.endpoint = endpoint;
        this.regionId = regionId;

        this.bucketDef = bucket;

        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    @Override
    public String getString(String bucket, String key) throws CloudFileException {
        if (Utils.isEmpty(bucket)) {
            bucket = bucketDef;
        }

        String date = Datetime.Now().toGmtString();

        String objPath = "/" + bucket + "/" + key;
        String url = buildUrl(bucket, key);

        String Signature = (hmacSha1(buildSignData("GET", date, objPath, null), secretKey));

        String Authorization = "OSS " + accessKey + ":" + Signature;

        Map<String, String> head = new HashMap<String, String>();
        head.put("Date", date);
        head.put("Authorization", Authorization);

        try {
            return HttpUtils.http(url)
                    .header("Date", date)
                    .header("Authorization", Authorization)
                    .get();
        } catch (Exception ex) {
            throw new CloudFileException(ex);
        }
    }

    @Override
    public Result putString(String bucket, String key, String content) throws CloudFileException {
        if (Utils.isEmpty(bucket)) {
            bucket = bucketDef;
        }

        String date = Datetime.Now().toGmtString();

        String objPath = "/" + bucket + "/" + key;
        String url = buildUrl(bucket, key);
        String contentType = "text/plain; charset=utf-8";

        String Signature = (hmacSha1(buildSignData("PUT", date, objPath, contentType), secretKey));
        String Authorization = "OSS " + accessKey + ":" + Signature;

        try {
            String tmp = HttpUtils.http(url)
                    .header("Date", date)
                    .header("Authorization", Authorization)
                    .bodyTxt(content, contentType)
                    .put();

            return Result.succeed(tmp);
        } catch (Exception ex) {
            throw new CloudFileException(ex);
        }
    }

    @Override
    public Result putFile(String bucket, String key, File file) throws CloudFileException {
        if (Utils.isEmpty(bucket)) {
            bucket = bucketDef;
        }

        String date = Datetime.Now().toGmtString();

        String objPath = "/" + bucket + "/" + key;
        String url = buildUrl(bucket, key);

        String contentType = null;
        try {
            contentType = Files.probeContentType(file.toPath());
        } catch (IOException ex) {

        }

        if(contentType == null){
            contentType = "text/plain; charset=utf-8";
        }

        String Signature = (hmacSha1(buildSignData("PUT", date, objPath, contentType), secretKey));
        String Authorization = "OSS " + accessKey + ":" + Signature;

        try {
            String tmp = HttpUtils.http(url)
                    .header("Date", date)
                    .header("Authorization", Authorization)
                    .bodyRaw(new FileInputStream(file), contentType)
                    .put();

            return Result.succeed(tmp);
        } catch (Exception ex) {
            throw new CloudFileException(ex);
        }
    }

    private String buildUrl(String bucket, String key) {
        if (endpoint.startsWith(bucket)) {
            return "http://" + endpoint + "/" + key;
        } else {
            return "http://" + bucket + "." + endpoint + "/" + key;
        }
    }

    private String hmacSha1(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), ALGORITHM);
            mac.init(keySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes(CHARSET_UTF8));

            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String buildSignData(String method, String date, String objPath, String contentType) {
        if (contentType == null) {
            return method + "\n\n\n"
                    + date + "\n"
                    + objPath;
        } else {
            return method + "\n\n"
                    + contentType + "\n"
                    + date + "\n"
                    + objPath;
        }
    }
}
