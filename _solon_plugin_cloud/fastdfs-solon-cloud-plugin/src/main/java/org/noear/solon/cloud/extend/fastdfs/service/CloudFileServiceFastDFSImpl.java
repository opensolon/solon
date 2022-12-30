package org.noear.solon.cloud.extend.fastdfs.service;

import org.csource.fastdfs.*;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.exception.CloudFileException;
import org.noear.solon.cloud.model.Media;
import org.noear.solon.cloud.service.CloudFileService;
import org.noear.solon.core.handle.Result;

import java.net.URL;
import java.util.Properties;

/**
 * 云文件服务 FastDFS
 *
 * @author liaocp
 * @since 1.12
 */
public class CloudFileServiceFastDFSImpl implements CloudFileService {
    private final String bucketDef;

    private final StorageClient client;

    /**
     * 获取真实客户端
     */
    public StorageClient getClient() {
        return client;
    }

    public CloudFileServiceFastDFSImpl(CloudProps cloudProps) {
        bucketDef = cloudProps.getFileBucket();

        URL propUrl = Utils.getResource("fastdfs.yml");
        if (propUrl == null) {
            propUrl = Utils.getResource("fastdfs.properties");
        }

        if (propUrl == null) {
            propUrl = Utils.getResource("META-INF/solon_def/fastdfs_def.properties");
        }

        //检测是否有配置文件
        if (propUrl == null) {
            throw new CloudFileException("Missing configuration files: 'fastdfs.properties' or 'fastdfs.yml'");
        }

        Properties props = Utils.loadProperties(propUrl);

        //构建 servers
        String servers = props.getProperty("fastdfs.tracker_servers");
        if (Utils.isEmpty(servers)) {
            props.setProperty("fastdfs.tracker_servers", cloudProps.getFileEndpoint());
        }

        try {
            ClientGlobal.initByProperties(props);

            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getTrackerServer();
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            client = new StorageClient(trackerServer, storageServer);
        } catch (Exception e) {
            throw new CloudFileException(e);
        }
    }

    @Override
    public Media get(String bucket, String key) throws CloudFileException {
        if (Utils.isEmpty(bucket)) {
            bucket = bucketDef;
        }

        try {
            byte[] resultByte = client.download_file(bucket, key);
            return new Media(resultByte);
        } catch (Exception e) {
            throw new CloudFileException("Cloud file get failure: " + key, e);
        }
    }

    @Override
    public Result<Object> put(String bucket, String key, Media media) throws CloudFileException {
        if (Utils.isEmpty(bucket)) {
            bucket = bucketDef;
        }

        String sufName = null;
        int sufIdx = key.lastIndexOf(".");

        if (sufIdx > 0) {
            sufName = key.substring(sufIdx + 1);
        }

        if (Utils.isEmpty(sufName)) {
            throw new CloudFileException("the file extension must not be empty");
        }

        String[] result;
        try {
            result = client.upload_file(bucket, media.bodyAsBytes(), sufName, null);
        } catch (Exception e) {
            throw new CloudFileException("Cloud file put failure: " + key, e);
        }

        if (result == null) {
            throw new CloudFileException("Cloud file put failure code[" + client.getErrorCode() + "]: " + key);
        } else {
            return Result.succeed(result);
        }
    }

    @Override
    public Result<Object> delete(String bucket, String key) throws CloudFileException {
        if (Utils.isEmpty(bucket)) {
            bucket = bucketDef;
        }

        try {
            int tmp = client.delete_file(bucket, key);

            return Result.succeed(tmp);
        } catch (Exception e) {
            throw new CloudFileException("Cloud file delete failure: " + key, e);
        }
    }
}