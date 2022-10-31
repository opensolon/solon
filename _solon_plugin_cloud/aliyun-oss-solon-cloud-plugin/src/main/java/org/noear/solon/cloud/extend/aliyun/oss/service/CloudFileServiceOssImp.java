package org.noear.solon.cloud.extend.aliyun.oss.service;

import okhttp3.ResponseBody;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.exception.CloudFileException;
import org.noear.solon.cloud.model.Media;
import org.noear.solon.cloud.service.CloudFileService;
import org.noear.solon.cloud.utils.http.HttpUtils;
import org.noear.solon.core.handle.Result;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 云端文件服务（aliyun oss）
 *
 * @author noear
 * @since 1.3
 */
public class CloudFileServiceOssImp implements CloudFileService {
    private final String bucketDef;

    private final String accessKey;
    private final String secretKey;
    private final String endpoint;

    protected String CHARSET_UTF8 = "utf8";
    protected String ALGORITHM = "HmacSHA1";


    public CloudFileServiceOssImp(CloudProps cloudProps) {
        this(
                cloudProps.getFileEndpoint(),
                cloudProps.getFileBucket(),
                cloudProps.getFileAccessKey(),
                cloudProps.getFileSecretKey()
        );
    }


    public CloudFileServiceOssImp(String endpoint, String bucket, String accessKey, String secretKey) {
        if(Utils.isEmpty(endpoint)){
            throw new IllegalArgumentException("The endpoint configuration is missing");
        }

        this.endpoint = endpoint;

        this.bucketDef = bucket;

        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }


    @Override
    public Media get(String bucket, String key) throws CloudFileException {
        if (Utils.isEmpty(bucket)) {
            bucket = bucketDef;
        }

        try {
            String date = Datetime.Now().toGmtString();

            String objPath = "/" + bucket + "/" + key;
            String url = buildUrl(bucket, key);

            String Signature = (hmacSha1(buildSignData("GET", date, objPath, null), secretKey));

            String Authorization = "OSS " + accessKey + ":" + Signature;

            Map<String, String> head = new HashMap<String, String>();
            head.put("Date", date);
            head.put("Authorization", Authorization);

            ResponseBody obj = HttpUtils.http(url)
                    .header("Date", date)
                    .header("Authorization", Authorization)
                    .exec("GET").body();

            return new Media(obj.byteStream(), obj.contentType().toString());
        } catch (IOException ex) {
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
            String date = Datetime.Now().toGmtString();

            String objPath = "/" + bucket + "/" + key;
            String url = buildUrl(bucket, key);

            String Signature = (hmacSha1(buildSignData("PUT", date, objPath, streamMime), secretKey));
            String Authorization = "OSS " + accessKey + ":" + Signature;


            String tmp = HttpUtils.http(url)
                    .header("Date", date)
                    .header("Authorization", Authorization)
                    .bodyRaw(media.body(), streamMime)
                    .put();

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

        try {
            String date = Datetime.Now().toGmtString();

            String objPath = "/" + bucket + "/" + key;
            String url = buildUrl(bucket, key);

            String Signature = (hmacSha1(buildSignData("DELETE", date, objPath, null), secretKey));

            String Authorization = "OSS " + accessKey + ":" + Signature;

            String tmp = HttpUtils.http(url)
                    .header("Date", date)
                    .header("Authorization", Authorization)
                    .delete();

            return Result.succeed(tmp);
        } catch (IOException ex) {
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
