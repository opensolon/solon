package org.noear.solon.cloud.extend.aws.s3.service;

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
import java.net.URI;
import java.util.Base64;

/**
 * @author noear
 * @since 1.5
 */
public class CloudFileServiceOfS3HttpImpl implements CloudFileService {
    private final static String CHARSET_UTF8 = "utf8";
    private final static String ALGORITHM = "HmacSHA1";

    private final static String acl_header = "x-amz-grant-read";
    private final static String acl_header_val = "uri=\"http://acs.amazonaws.com/groups/global/AllUsers\"";
    private final static String acl_canonicalized = "x-amz-grant-read:uri=\"http://acs.amazonaws.com/groups/global/AllUsers\"\n";


    private final String bucketDef;

    private final String endpoint; //endpoint 或 regionId 必须要有一个
    private final String regionId;

    private final String accessKey;
    private final String secretKey;

    protected final boolean isHttps;


    public CloudFileServiceOfS3HttpImpl(CloudProps cloudProps) {
        String endpointStr = cloudProps.getFileEndpoint();

        this.regionId = cloudProps.getFileRegionId();

        this.bucketDef = cloudProps.getFileBucket();

        this.accessKey = cloudProps.getFileAccessKey();
        this.secretKey = cloudProps.getFileSecretKey();

        if (Utils.isEmpty(regionId) && Utils.isEmpty(endpointStr)) {
            throw new CloudFileException("The 'regionId' and 'endpoint' configuration must have one");
        }

        if (Utils.isEmpty(endpointStr)) {
            isHttps = true;
        } else {
            if (endpointStr.startsWith("http://")) {
                isHttps = false;
            } else {
                isHttps = true;
            }

            if (endpointStr.contains("://")) {
                endpointStr = URI.create(endpointStr).getHost();
            }
        }

        this.endpoint = endpointStr;
    }

    @Override
    public Media get(String bucket, String key) throws CloudFileException {
        if (Utils.isEmpty(bucket)) {
            bucket = bucketDef;
        }

        try {
            String objPath = "/" + bucket + "/" + key;
            String date = Datetime.Now().toGmtString();

            String stringToSign = buildSignData("GET", null, null, date, null, objPath);
            String signature = hmacSha1(stringToSign, secretKey);
            String authorization = "AWS " + accessKey + ":" + signature;

            String url = buildUrl(bucket, key);

            ResponseBody obj = HttpUtils.http(url)
                    .header("Date", date)
                    .header("Authorization", authorization)
                    .exec("GET").body();

            return new Media(obj.byteStream(), obj.contentType().toString());
        } catch (Exception ex) {
            throw new CloudFileException(ex);
        }
    }

    @Override
    public Result put(String bucket, String key, Media media) throws CloudFileException {
        if (Utils.isEmpty(bucket)) {
            bucket = bucketDef;
        }

        String contentType = media.contentType();
        if (Utils.isEmpty(contentType)) {
            contentType = "text/plain; charset=utf-8";
        }

        try {
            String objPath = "/" + bucket + "/" + key;
            String date = Datetime.Now().toGmtString();

//            Signature = Base64( HMAC-SHA1( YourSecretAccessKeyID, UTF-8-Encoding-Of( StringToSign ) ) );

            String stringToSign = buildSignData("PUT", null, contentType, date, acl_canonicalized, objPath);
            String signature = hmacSha1(stringToSign, secretKey);
            String authorization = "AWS " + accessKey + ":" + signature;

            String url = buildUrl(bucket, key);

            String tmp = HttpUtils.http(url)
                    .header("Date", date)
                    .header("Authorization", authorization)
                    .header(acl_header, acl_header_val)
                    .bodyRaw(media.body(), contentType)
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

            String stringToSign = buildSignData("DELETE", null, null, date, null, objPath);
            String signature = hmacSha1(stringToSign, secretKey);
            String authorization = "AWS " + accessKey + ":" + signature;

            String url = buildUrl(bucket, key);

            String tmp = HttpUtils.http(url)
                    .header("Date", date)
                    .header("Authorization", authorization)
                    .delete();

            return Result.succeed(tmp);
        } catch (Exception ex) {
            throw new CloudFileException(ex);
        }
    }

    private String buildUrl(String bucket, String key) {
        StringBuilder buf = new StringBuilder(100);
        if (isHttps) {
            buf.append("https://");
        } else {
            buf.append("http://");
        }

        buf.append(bucket).append(".");

        if (Utils.isEmpty(endpoint)) {
            buf.append("s3.");
            buf.append(regionId);
            buf.append(".amazonaws.com/");
            buf.append(key);
        } else {
            buf.append(endpoint);
            buf.append("/").append(key);
        }

        return buf.toString();
    }

    private String hmacSha1(String data, String secretKey) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
            mac.init(keySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes(CHARSET_UTF8));

            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String buildSignData(String httpVerb, String contentMd5, String contentType, String date,
                                 String canonicalizedAmzHeaders, String canonicalizedResource) {

        StringBuilder sb = new StringBuilder();
        sb.append(httpVerb + "\n");

        if (Utils.isNotEmpty(contentMd5)) {
            sb.append(contentMd5 + "\n");
        } else {
            sb.append("\n");
        }
        if (Utils.isNotEmpty(contentType)) {
            sb.append(contentType + "\n");
        } else {
            sb.append("\n");
        }
        if (Utils.isNotEmpty(date)) {
            sb.append(date + "\n");
        }
        if (Utils.isNotEmpty(canonicalizedAmzHeaders)) {
            sb.append(canonicalizedAmzHeaders);
        }
        if (Utils.isNotEmpty(canonicalizedResource)) {
            sb.append(canonicalizedResource);
        }
        return sb.toString();
    }
}
