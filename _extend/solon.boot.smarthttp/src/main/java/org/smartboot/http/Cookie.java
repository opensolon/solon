/*******************************************************************************
 * Copyright (c) 2017-2020, org.smartboot. All rights reserved.
 * project name: smart-http
 * file name: Cookie.java
 * Date: 2020-01-01
 * Author: sandao (zhengjunweimail@163.com)
 ******************************************************************************/

package org.smartboot.http;

import java.util.Date;

/**
 * @author 三刀
 * @version V1.0 , 2018/8/7
 */
public interface Cookie {

    String getName();

    String getValue();

    Cookie setValue(final String value);

    String getPath();

    Cookie setPath(final String path);

    String getDomain();

    Cookie setDomain(final String domain);

    Integer getMaxAge();

    Cookie setMaxAge(final Integer maxAge);

    boolean isDiscard();

    Cookie setDiscard(final boolean discard);

    boolean isSecure();

    Cookie setSecure(final boolean secure);

    int getVersion();

    Cookie setVersion(final int version);

    boolean isHttpOnly();

    Cookie setHttpOnly(final boolean httpOnly);

    Date getExpires();

    Cookie setExpires(final Date expires);

    String getComment();

    Cookie setComment(final String comment);
}