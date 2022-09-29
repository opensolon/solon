package org.noear.solon.cloud.extend.jedis;

import org.noear.solon.cloud.CloudProps;

/**
 * @author noear
 * @since 1.10
 */
public class JedisProps {
    public static final String GROUP_SPLIT_MART = ":";

    public static final CloudProps instance = new CloudProps("jedis");
}
