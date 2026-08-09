/*
 *  Copyright (C) [2024] smartboot [zhengjunweimail@163.com]
 *
 *  企业用户未经smartboot组织特别许可，需遵循Apache-2.0开源协议合理合法使用本项目。
 *
 *   Enterprise users are required to use this project reasonably
 *   and legally in accordance with the Apache-2.0 open source agreement
 *  without special permission from the smartboot organization.
 */

package tech.smartboot.feat.core.server;

/**
 * @author 三刀 zhengjunweimail@163.com
 * @version v1.0 3/7/25
 */
public interface Session {
    String DEFAULT_SESSION_COOKIE_NAME = "FEAT_SESSION";

    String getSessionId();

    void put(String key, String value);

    String get(String key);

    int getTimeout();

    void setTimeout(final int expiry);

    void invalidate();
}
