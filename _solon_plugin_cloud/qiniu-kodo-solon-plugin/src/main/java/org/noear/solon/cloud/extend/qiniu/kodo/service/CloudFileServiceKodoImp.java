package org.noear.solon.cloud.extend.qiniu.kodo.service;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import okhttp3.ResponseBody;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.exception.CloudFileException;
import org.noear.solon.cloud.model.Media;
import org.noear.solon.cloud.service.CloudFileService;
import org.noear.solon.cloud.utils.http.HttpUtils;
import org.noear.solon.core.handle.Result;

import java.io.IOException;

/**
 * @author noear
 */
public class CloudFileServiceKodoImp implements CloudFileService {

    protected final String bucketDef;
    protected final String regionId;
    protected final String accessKey;
    protected final String secretKey;
    protected final String endpoint;

    protected final Auth auth;
    protected final UploadManager uploadManager;
    protected final BucketManager bucketManager;

    public CloudFileServiceKodoImp(CloudProps cloudProps) {
        this(cloudProps, null);
    }

    public CloudFileServiceKodoImp(CloudProps cloudProps, Region region) {
        bucketDef = cloudProps.getFileBucket();
        regionId  = cloudProps.getFileRegionId();
        accessKey = cloudProps.getFileAccessKey();
        secretKey = cloudProps.getFileSecretKey();
        endpoint = cloudProps.getFileEndpoint();

        auth = Auth.create(accessKey, secretKey);

        //构造一个带指定 Region 对象的配置类
        Configuration cfg = buildConfig(region);

        //...其他参数参考类注释
        uploadManager = new UploadManager(cfg);
        bucketManager = new BucketManager(auth, cfg);
    }

    public Configuration buildConfig(Region region) {
        if (region != null) {
            return new Configuration(region);
        }

        switch (regionId) {
            case "z0":
                return new Configuration(Region.region0());
            case "huadong":
                return new Configuration(Region.huadong());

            case "cn-east-2":
                return new Configuration(Region.regionCnEast2());
            case "zhejiang2":
                return new Configuration(Region.huadongZheJiang2());

            case "z1":
                return new Configuration(Region.region1());
            case "huabei":
                return new Configuration(Region.huabei());

            case "z2":
                return new Configuration(Region.region2());
            case "huanan":
                return new Configuration(Region.huanan());

            case "na0":
                return new Configuration(Region.regionNa0());
            case "beimei":
                return new Configuration(Region.beimei());

            case "as0":
            case "xinjiapo":
                return new Configuration(Region.xinjiapo());

            case "fog-cn-east-1":
                return new Configuration(Region.regionFogCnEast1());

            default:
                return new Configuration();
        }
    }


    @Override
    public Media get(String bucket, String key) throws CloudFileException {
        if (Utils.isEmpty(bucket)) {
            bucket = bucketDef;
        }

        String baseUrl = buildUrl(key);
        String downUrl = auth.privateDownloadUrl(baseUrl);

        try {
            ResponseBody obj = HttpUtils.http(downUrl).exec("GET").body();

            return new Media(obj.byteStream(), obj.contentType().toString());
        } catch (IOException e) {
            throw new CloudFileException(e);
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

        String uploadToken = auth.uploadToken(bucket);

        try {
            Response resp = uploadManager.put(media.body(), key, uploadToken, new StringMap(), streamMime);
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
