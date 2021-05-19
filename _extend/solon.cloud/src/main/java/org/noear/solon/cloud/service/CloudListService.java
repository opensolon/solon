package org.noear.solon.cloud.service;

/**
 * 云端名单服务（安全名单，非法名单，黑名单，白名单）
 *
 * @author noear
 * @since 1.3
 */
public interface CloudListService {
    /**
     * 在名单中
     *
     * @param names 名称
     * @param type 检测类型
     * @param value 值
     */
    boolean inList(String names, String type, String value);

    /**
     * 在IP名单中
     * */
    default boolean inListOfIp(String names, String ip) {
        return inList(names, "ip", ip);
    }

    /**
     * 在IP名单中
     * */
    default boolean inListOfServerIp(String ip) {
        return inList("server", "ip", ip);
    }

    /**
     * 在IP名单中
     * */
    default boolean inListOfClientIp(String ip) {
        return inList("client", "ip", ip);
    }

    /**
     * 在IP名单中
     * */
    default boolean inListOfClientAndServerIp(String ip){
        return inList("client,server","ip",ip);
    }

    /**
     * 在手机名单中
     * */
    default boolean inListOfMobile(String names, String mobile) {
        return inList(names, "mobile", mobile);
    }

    /**
     * 在域名单中
     * */
    default boolean inListOfDomain(String names, String domain) {
        return inList(names, "domain", domain);
    }
}
