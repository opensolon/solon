package org.noear.solon.cloud.model;

import org.noear.solon.Solon;
import org.noear.solon.cloud.utils.LocalUtils;

import java.io.Serializable;

/**
 * 服务节点模型
 *
 * @author noear
 * @since 1.2
 */
public class Node implements Serializable {
    /**
     * 服务名
     */
    public String service;

    /**
     * 地址（ip:port）
     */
    public String address;

    /**
     * 协议（http, ws, tcp...）
     */
    public String protocol;

    /**
     * 权重
     */
    public double weight = 1.0D;

    /**
     * 元信息
     */
    public String meta;


    private static Node local;
    public static Node local(){
        if(local == null){
            local = new Node();
            local.address = LocalUtils.getLocalAddress() + ":"+Solon.global().port();
            local.service = Solon.cfg().appName();
        }

        return local;
    }
}
