package org.noear.solon.cloud.extend.aliyun.oss.service;

import org.noear.solon.cloud.extend.aliyun.oss.OssProps;
import org.noear.solon.cloud.service.CloudFileService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 云端文件服务
 *
 * @author noear
 * @since 1.3
 */
public class CloudFileServiceImp implements CloudFileService {
    protected final String bucket;
    protected final String accessKey;
    protected final String secretKey;
    protected final String endpoint;

    protected String CHARSET_UTF8 = "utf8";
    protected String ALGORITHM = "HmacSHA1";

    public CloudFileServiceImp(){
        this.bucket = OssProps.instance.getFileBucket();
        this.endpoint = OssProps.instance.getFileEndpoint();
        this.accessKey = OssProps.instance.getFileAccessKey();
        this.secretKey = OssProps.instance.getFileSecretKey();
    }

    public CloudFileServiceImp(String endpoint, String bucket, String accessKey, String secretKey) {
        this.bucket = bucket;
        this.endpoint = endpoint;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    @Override
    public String getString(String key) throws Exception {
        String date = Datetime.Now().toGmtString();

        String objPath = "/" + bucket + key;
        String url = buildUrl(key);

        String Signature = (hmacSha1(buildSignData("GET", date, objPath, null), secretKey));

        String Authorization = "OSS " + accessKey + ":" + Signature;

        Map<String, String> head = new HashMap<String, String>();
        head.put("Date", date);
        head.put("Authorization", Authorization);

        return HttpUtils.http(url)
                .header("Date", date)
                .header("Authorization", Authorization)
                .get();
    }

    @Override
    public String putString(String key, String content) throws Exception {
        String date = Datetime.Now().toGmtString();

        String objPath = "/" + bucket + key;
        String url = buildUrl(key);
        String contentType = "text/plain; charset=utf-8";

        String Signature = (hmacSha1(buildSignData("PUT", date, objPath, contentType), secretKey));
        String Authorization = "OSS " + accessKey + ":" + Signature;

        return HttpUtils.http(url)
                .header("Date", date)
                .header("Authorization", Authorization)
                .bodyTxt(content, contentType)
                .put();
    }

    @Override
    public String putFile(String key, File file) throws Exception {
        String date = Datetime.Now().toGmtString();

        String objPath = "/" + bucket + key;
        String url = buildUrl(key);
        String contentType = "text/plain; charset=utf-8";

        String Signature = (hmacSha1(buildSignData("PUT", date, objPath, contentType), secretKey));
        String Authorization = "OSS " + accessKey + ":" + Signature;

        return HttpUtils.http(url)
                .header("Date", date)
                .header("Authorization", Authorization)
                .bodyRaw(new FileInputStream(file), contentType)
                .put();
    }

    private String buildUrl(String key) {
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
