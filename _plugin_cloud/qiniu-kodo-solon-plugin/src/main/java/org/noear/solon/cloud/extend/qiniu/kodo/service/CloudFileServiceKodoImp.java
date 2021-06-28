package org.noear.solon.cloud.extend.qiniu.kodo.service;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.cloud.exception.CloudFileException;
import org.noear.solon.cloud.extend.qiniu.kodo.KodoProps;
import org.noear.solon.cloud.service.CloudFileService;
import org.noear.solon.cloud.tool.HttpUtils;
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

        String uploadToken = auth.uploadToken(bucket);

        try {
            StringMap stringMap = new StringMap();
            Response response = uploadManager.put(stream, key, uploadToken, stringMap, streamMime);
            //解析上传成功的结果
            DefaultPutRet putRet = ONode.deserialize(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
        }
        return null;
    }

    private String buildUrl(String key) {
        return "https://" + endpoint + "/" + key;
    }
}
