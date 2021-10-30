package org.noear.solon.cloud.extend.aws.s3.service;

import okhttp3.ResponseBody;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.exception.CloudFileException;
import org.noear.solon.cloud.extend.aws.s3.S3Props;
import org.noear.solon.cloud.model.Media;
import org.noear.solon.cloud.service.CloudFileService;
import org.noear.solon.cloud.utils.http.HttpUtils;
import org.noear.solon.core.handle.Result;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * @author noear
 * @since 1.5
 */
public class CloudFileServiceS3OfHttpImp implements CloudFileService {
    public static CloudFileServiceS3OfHttpImp instance;

    public static synchronized CloudFileServiceS3OfHttpImp getInstance() {
        if (instance == null) {
            instance = new CloudFileServiceS3OfHttpImp();
        }

        return instance;
    }

    final static String CHARSET_UTF8 = "utf8";
    final static String ALGORITHM = "HmacSHA1";

    final static String acl_header = "x-amz-grant-read";
    final static String acl_header_val = "uri=\"http://acs.amazonaws.com/groups/global/AllUsers\"";
    final static String acl_canonicalized = "x-amz-grant-read:uri=\"http://acs.amazonaws.com/groups/global/AllUsers\"\n";


    protected final String bucketDef;

    protected final String accessKey;
    protected final String secretKey;
    protected final String regionId;


    /**
     * 限内部使用
     * */
    private CloudFileServiceS3OfHttpImp() {
        this(S3Props.instance);
    }

    public CloudFileServiceS3OfHttpImp(CloudProps cloudProps) {
        this(
                cloudProps.getFileRegionId(),
                cloudProps.getFileBucket(),
                cloudProps.getFileAccessKey(),
                cloudProps.getFileSecretKey()
        );
    }

    public CloudFileServiceS3OfHttpImp(String regionId, String bucket, String accessKey, String secretKey) {
        this.regionId = regionId;

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
        return "http://" + bucket + ".s3." + regionId + ".amazonaws.com" + "/" + key;
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
