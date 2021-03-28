package org.noear.solon.cloud.service;

/**
 * 云端名单服务
 *
 * @author noear
 * @since 1.3
 */
public interface CloudListService {
    /**
     * 在名单中
     *
     * @param name 名称
     * @param type 检测类型
     * @param value 值
     */
    boolean inList(String name, String type, String value);

    /**
     * 在IP名单中
     * */
    default boolean inListOfIp(String name, String ip) {
        return inList(name, "ip", ip);
    }

    /**
     * 在手机名单中
     * */
    default boolean inListOfMobile(String name, String mobile) {
        return inList(name, "mobile", mobile);
    }

    /**
     * 在域名单中
     * */
    default boolean inListOfDomain(String name, String domain) {
        return inList(name, "domain", domain);
    }
}
