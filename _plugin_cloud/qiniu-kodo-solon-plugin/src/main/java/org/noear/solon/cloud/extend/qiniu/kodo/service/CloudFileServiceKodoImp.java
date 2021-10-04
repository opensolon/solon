package org.noear.solon.cloud.extend.qiniu.kodo.service;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.noear.solon.Utils;
import org.noear.solon.cloud.exception.CloudFileException;
import org.noear.solon.cloud.extend.qiniu.kodo.KodoProps;
import org.noear.solon.cloud.service.CloudFileService;
import org.noear.solon.cloud.utils.http.HttpUtils;
import org.noear.solon.core.handle.Result;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author noear
 */
public class CloudFileServiceKodoImp implements CloudFileService {
    private static CloudFileServiceKodoImp instance;

    public static synchronized CloudFileServiceKodoImp getInstance() {
        if (instance == null) {
            instance = new CloudFileServiceKodoImp();
        }

        return instance;
    }


    protected final String bucketDef;
    protected final String accessKey;
    protected final String secretKey;
    protected final String endpoint;

    protected final Auth auth;
    protected final UploadManager uploadManager;
    protected final BucketManager bucketManager;

    public CloudFileServiceKodoImp() {
        bucketDef = KodoProps.instance.getFileBucket();
        accessKey = KodoProps.instance.getFileAccessKey();
        secretKey = KodoProps.instance.getFileSecretKey();
        endpoint = KodoProps.instance.getFileEndpoint();

        auth = Auth.create(accessKey, secretKey);

        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region0());
        //...其他参数参考类注释
        uploadManager = new UploadManager(cfg);
        bucketManager = new BucketManager(auth, cfg);
    }

    @Override
    public InputStream getStream(String bucket, String key) throws CloudFileException {
        if (Utils.isEmpty(bucket)) {
            bucket = bucketDef;
        }

        String baseUrl = buildUrl(key);
        String downUrl = auth.privateDownloadUrl(baseUrl);

        try {
            okhttp3.Response response = HttpUtils.http(downUrl).exec("GET");
            return response.body().byteStream();
        } catch (IOException e) {
            throw new CloudFileException(e);
        }
    }

    @Override
    public Result putStream(String bucket, String key, InputStream stream, String streamMime) throws CloudFileException {
        if (Utils.isEmpty(bucket)) {
            bucket = bucketDef;
        }

        if (Utils.isEmpty(streamMime)) {
            streamMime = "text/plain; charset=utf-8";
        }

        String uploadToken = auth.uploadToken(bucket);

        try {
            Response resp = uploadManager.put(stream, key, uploadToken, new StringMap(), streamMime);
            return Result.succeed(resp.bodyString());
        } catch (QiniuException e) {
            throw new CloudFileException(e);
        }
    }

    @Override
    public Result delete(String bucket, String key) throws CloudFileException {
        if (Utils.isEmpty(bucket)) {
            bucket = bucketDef;
        }

        try {
            Response resp = bucketManager.delete(bucket, key);
            return Result.succeed(resp.bodyString());
        } catch (QiniuException e) {
            return Result.failure(e.error());
        }
    }

    private String buildUrl(String key) {
        if (endpoint.contains("://")) {
            return endpoint + "/" + key;
        } else {
            return "https://" + endpoint + "/" + key;
        }
    }
}
