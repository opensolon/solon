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
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
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
    private static CloudFileServiceOssImp instance;
    public static synchronized CloudFileServiceOssImp getInstance() {
        if (instance == null) {
            instance = new CloudFileServiceOssImp();
        }

        return instance;
    }


    static final FileNameMap contentTypeMap = URLConnection.getFileNameMap();

    protected final String bucketDef;
    protected final String accessKey;
    protected final String secretKey;
    protected final String endpoint;

    protected String CHARSET_UTF8 = "utf8";
    protected String ALGORITHM = "HmacSHA1";

    private CloudFileServiceOssImp() {
        this(
                OssProps.instance.getFileEndpoint(),
                OssProps.instance.getFileBucket(),
                OssProps.instance.getFileAccessKey(),
                OssProps.instance.getFileSecretKey()
        );
    }

    public CloudFileServiceOssImp(Properties pops) {
        this(
                pops.getProperty("endpoint"),
                pops.getProperty("bucket"),
                pops.getProperty("accessKey"),
                pops.getProperty("secretKey")
        );
    }


    public CloudFileServiceOssImp(String endpoint, String bucket, String accessKey, String secretKey) {
        this.endpoint = endpoint;

        this.bucketDef = bucket;

        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    @Override
    public String getText(String bucket, String key) throws CloudFileException {
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
    public Result putText(String bucket, String key, String content) throws CloudFileException {
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
        String contentType = contentTypeMap.getContentTypeFor(file.getName());
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(file);
        } catch (Exception ex) {
            throw new CloudFileException(ex);
        }

        return putFile(bucket, key, inputStream, contentType);
    }

    @Override
    public Result putFile(String bucket, String key, InputStream inputStream, String contentType) throws CloudFileException {
        if (Utils.isEmpty(bucket)) {
            bucket = bucketDef;
        }

        String date = Datetime.Now().toGmtString();

        String objPath = "/" + bucket + "/" + key;
        String url = buildUrl(bucket, key);

        if (contentType == null) {
            contentType = "text/plain; charset=utf-8";
        }

        String Signature = (hmacSha1(buildSignData("PUT", date, objPath, contentType), secretKey));
        String Authorization = "OSS " + accessKey + ":" + Signature;

        try {
            String tmp = HttpUtils.http(url)
                    .header("Date", date)
                    .header("Authorization", Authorization)
                    .bodyRaw(inputStream, contentType)
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
