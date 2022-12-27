package org.noear.solon.cloud.extend.fastdfs.service;

import org.csource.fastdfs.*;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.exception.CloudFileException;
import org.noear.solon.cloud.model.Media;
import org.noear.solon.cloud.service.CloudFileService;
import org.noear.solon.core.handle.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 云文件服务 FastDFS
 *
 * @author liaocp
 */
public class CloudFileServiceFastDFSImpl implements CloudFileService {

    private final static Logger LOGGER = LoggerFactory.getLogger(CloudFileServiceFastDFSImpl.class);

    // default group
    private static String DEFAULT_GROUP_NAME = "group1";

    private final StorageClient storageClient;

    public CloudFileServiceFastDFSImpl(CloudProps cloudProps) {
        String confPath = cloudProps.getValue("confPath");
        String groupName = cloudProps.getValue("groupName");
        if (Utils.isNotEmpty(groupName)) {
            DEFAULT_GROUP_NAME = groupName;
        }
        try {
            if (confPath.contains("properties")) {
                ClientGlobal.initByProperties(Utils.getResource(confPath).getFile());
            } else {
                ClientGlobal.init(Utils.getResource(confPath).getFile());
            }
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getTrackerServer();
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            storageClient = new StorageClient(trackerServer, storageServer);
        } catch (Exception e) {
            throw new CloudFileException(e);
        }
    }

    @Override
    public Media get(String bucket, String key) throws CloudFileException {
        if (Utils.isEmpty(bucket)) {
            bucket = DEFAULT_GROUP_NAME;
        }
        try {
            byte[] resultByte = storageClient.download_file(bucket, key);
            return new Media(resultByte);
        } catch (Exception e) {
            throw new CloudFileException("failed to download file from FastDFS");
        }
    }

    @Override
    public Result<Object> put(String bucket, String key, Media media) throws CloudFileException {
        if (Utils.isEmpty(bucket)) {
            bucket = DEFAULT_GROUP_NAME;
        }
        String extensionName = key.substring(key.lastIndexOf(".") + 1);
        if (Utils.isEmpty(extensionName)) {
            throw new CloudFileException("the file extension must not be empty");
        }
        String[] result;
        try {
            result = storageClient.upload_file(bucket, media.body().readAllBytes(), extensionName, null);
            if (result == null) {
                throw new CloudFileException("error code: " + storageClient.getErrorCode());
            }
        } catch (Exception e) {
            LOGGER.error("upload file failed", e);
            return Result.failure(e.getLocalizedMessage());
        }
        return Result.succeed(result);
    }

    @Override
    public Result<Object> delete(String bucket, String key) throws CloudFileException {
        if (Utils.isEmpty(bucket)) {
            bucket = DEFAULT_GROUP_NAME;
        }
        try {
            storageClient.delete_file(bucket, key);
            return Result.succeed();
        } catch (Exception e) {
            LOGGER.error("failed to delete file", e);
            throw new CloudFileException("failed to delete file");
        }
    }
}
