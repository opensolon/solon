/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.common.multipart;


import tech.smartboot.feat.core.common.FeatUtils;

import java.nio.file.Paths;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public class MultipartConfig {

    private String location;
    private long maxFileSize;
    private long maxRequestSize;

    public MultipartConfig() {
    }

    /**
     * Constructs an instance with all values specified.
     *
     * @param location          the directory location where files will be stored
     * @param maxFileSize       the maximum size allowed for uploaded files
     * @param maxRequestSize    the maximum size allowed for multipart/form-data requests
     */
    public MultipartConfig(String location, long maxFileSize, long maxRequestSize) {
        if (location == null) {
            this.location = "";
        } else {
            this.location = location;
        }
        if (FeatUtils.isNotBlank(this.location) && !Paths.get(this.location).isAbsolute()) {
            throw new IllegalStateException("location must be absolute");
        }

        this.maxFileSize = maxFileSize;
        this.maxRequestSize = maxRequestSize;
    }


    /**
     * Gets the directory location where files will be stored.
     *
     * @return the directory location where files will be stored
     */
    public String getLocation() {
        return this.location;
    }

    /**
     * Gets the maximum size allowed for uploaded files.
     *
     * @return the maximum size allowed for uploaded files
     */
    public long getMaxFileSize() {
        return this.maxFileSize;
    }

    /**
     * Gets the maximum size allowed for multipart/form-data requests.
     *
     * @return the maximum size allowed for multipart/form-data requests
     */
    public long getMaxRequestSize() {
        return this.maxRequestSize;
    }

}
