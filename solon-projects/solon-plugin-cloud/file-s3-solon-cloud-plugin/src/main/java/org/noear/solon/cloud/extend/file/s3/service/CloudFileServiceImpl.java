package org.noear.solon.cloud.extend.file.s3.service;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.exception.CloudFileException;
import org.noear.solon.cloud.model.Media;
import org.noear.solon.cloud.service.CloudFileService;
import org.noear.solon.core.Props;
import org.noear.solon.core.handle.Result;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * CloudFileService 综合实现
 *
 * @author 等風來再離開
 * @since 1.11
 */
public class CloudFileServiceImpl implements CloudFileService {
    private Map<String, CloudFileService> bucketServiceMap = new HashMap<>();
    private CloudProps cloudProps;

    public CloudFileServiceImpl(CloudProps cloudProps) {
        this.cloudProps = cloudProps;

        Map<String, Props> propsMap = cloudProps.getProp("file.buckets")
                .getGroupedProp("");

        for (Map.Entry<String, Props> kv : propsMap.entrySet()) {
            String bucketName = kv.getKey();
            Props props = kv.getValue();

            addBucket(bucketName, props);
        }
    }

    /**
     * 获取默认 bucket
     * */
    public String getBucketDef(){
        return cloudProps.getValue("file.default");
    }

    /**
     * 添加 bucket
     * */
    public void addBucket(String bucketName, Props props){
        String endpoint = props.getProperty("endpoint");

        // TODO!!!
        if (Utils.isNotEmpty(endpoint)) {
            if (endpoint.startsWith("http://") || endpoint.startsWith("https://")) {
                bucketServiceMap.put(bucketName, new CloudFileServiceOfS3SdkImpl(bucketName, props));
            } else {
                bucketServiceMap.put(bucketName, new CloudFileServiceOfLocalImpl(bucketName, props));
            }
        }
    }

    /**
     * 获取 bucket service
     * */
    public CloudFileService getBucketService(String bucket) throws CloudFileException {
        CloudFileService tmp = bucketServiceMap.get(bucket);

        if (tmp == null) {
            throw new CloudFileException("The configuration bucket is not configured: " + bucket);
        } else {
            return tmp;
        }
    }

    @Override
    public boolean exists(String bucket, String key) throws CloudFileException {
        if (Utils.isEmpty(bucket)) {
            bucket = getBucketDef();
        }

        CloudFileService tmp = getBucketService(bucket);
        return tmp.exists(bucket, key);
    }

    @Override
    public String getTempUrl(String bucket, String key, Date expiration) throws CloudFileException {
        if (Utils.isEmpty(bucket)) {
            bucket = getBucketDef();
        }

        CloudFileService tmp = getBucketService(bucket);
        return tmp.getTempUrl(bucket, key, expiration);
    }

    @Override
    public Media get(String bucket, String key) throws CloudFileException {
        if (Utils.isEmpty(bucket)) {
            bucket = getBucketDef();
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
            bucket = getBucketDef();
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
            bucket = getBucketDef();
        }

        CloudFileService tmp = getBucketService(bucket);
        return tmp.delete(bucket, key);
    }
}