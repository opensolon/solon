package org.noear.solon.extend.cloud;

/**
 * @author noear 2021/1/15 created
 */
public interface CloudProps {
    String type = "solon.cloud.type";
    String discoveryServer = "solon.cloud.discovery.server";
    String discoveryHostname = "solon.cloud.discovery.hostname";
    String discoveryTags = "solon.cloud.discovery.tags";
    String discoveryHealthCheckInterval = "solon.cloud.discovery.healthCheckInterval";
}
