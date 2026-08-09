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

import tech.smartboot.feat.core.common.HeaderName;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;


/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0.0
 */
public interface Part {

    InputStream getInputStream() throws IOException;

    String getContentType();


    String getName();


    String getSubmittedFileName();


    long getSize();


    void write(String fileName) throws IOException;


    void delete() throws IOException;

    default String getHeader(HeaderName name) {
        return getHeader(name.getName());
    }

    String getHeader(String name);


    Collection<String> getHeaders(String name);


    Collection<String> getHeaderNames();

}
