package org.noear.solon.extend.vaptcha.entities;

import org.noear.solon.validation.annotation.NotBlank;

/**
 * @author iYarnFog
 * @since 1.5
 */
public class iVaptcha {
    @NotBlank
    String token;
    @NotBlank
    String server;

    String realIp; //不直接赋值，由处理的地方控制

    /**
     * 领牌
     * */
    public String getToken() {
        return token;
    }

    /**
     * 服务地址
     * */
    public String getServer() {
        return server;
    }


    /**
     * 用户真实Id
     * */
    public String getRealIp() {
        return realIp;
    }
}
