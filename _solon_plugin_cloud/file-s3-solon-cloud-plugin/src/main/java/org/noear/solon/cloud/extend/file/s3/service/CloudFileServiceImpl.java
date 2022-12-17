package org.noear.solon.cloud.extend.file.s3.service;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.exception.CloudFileException;
import org.noear.solon.cloud.model.Media;
import org.noear.solon.cloud.service.CloudFileService;
import org.noear.solon.core.Props;
import org.noear.solon.core.handle.Result;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.11
 */
public class CloudFileServiceImpl implements CloudFileService {
    private String bucketDef;
    private Map<String, CloudFileService> bucketServiceMap = new HashMap<>();

    public CloudFileServiceImpl(CloudProps cloudProps) {
        bucketDef = cloudProps.getFileBucket();


        Map<String, Props> propsMap = cloudProps.getProp("file").getGroupedProp("buckets");
        for (Map.Entry<String, Props> kv : propsMap.entrySet()) {
            String bucketName = kv.getKey();
            Props props = kv.getValue();
            String endpoint = props.getProperty("endpoint");

            if (Utils.isNotEmpty(endpoint)) {
                if (endpoint.contains("/") && endpoint.contains(":") == false) {
                    //有 '/' 且没有 ':'
                    bucketServiceMap.put(bucketName, new CloudFileLocalClient(bucketName, props));
                } else {
                    bucketServiceMap.put(bucketName, new CloudFileS3Client(bucketName, props));
                }
            }
        }
    }

    private CloudFileService getBucketService(String bucket) throws CloudFileException {
        CloudFileService tmp = bucketServiceMap.get(bucket);

        if (tmp == null) {
            throw new CloudFileException("The configuration bucket is not configured: " + bucket);
        } else {
            return tmp;
        }
    }

    @Override
    public Media get(String bucket, String key) throws CloudFileException {
        if (Utils.isEmpty(bucket)) {
            bucket = bucketDef;
        }

        CloudFileService tmp = getBucketService(bucket);
        return tmp.get(bucket, key);
    }

    /**
     * @return Result 主要表达是否成功；尽量不用它途
     * */
    @Override
    public Result put(String bucket, String key, Media media) throws CloudFileException {
        if (Utils.isEmpty(bucket)) {
            bucket = bucketDef;
        }

        CloudFileService tmp = getBucketService(bucket);
        return tmp.put(bucket, key, media);
    }

    /**
     * @return Result 主要表达是否成功；尽量不用它途
     * */
    @Override
    public Result delete(String bucket, String key) throws CloudFileException {
        if (Utils.isEmpty(bucket)) {
            bucket = bucketDef;
        }

        CloudFileService tmp = getBucketService(bucket);
        return tmp.delete(bucket, key);
    }
}