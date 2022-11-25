package org.noear.solon.cloud.extend.file.s3.service;

import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.exception.CloudFileException;
import org.noear.solon.cloud.model.Media;
import org.noear.solon.cloud.service.CloudFileService;
import org.noear.solon.core.handle.Result;

/**
 * @author noear
 * @since 1.11
 */
public class CloudFileServiceS3Imp implements CloudFileService {
    public CloudFileServiceS3Imp(CloudProps cloudProps){

    }

    @Override
    public Media get(String bucket, String key) throws CloudFileException {
        return null;
    }

    @Override
    public Result put(String bucket, String key, Media media) throws CloudFileException {
        return null;
    }

    @Override
    public Result delete(String bucket, String key) throws CloudFileException {
        return null;
    }
}
