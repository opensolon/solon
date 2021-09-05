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

    public String getToken() {
        return token;
    }

    public String getServer() {
        return server;
    }




    //这个不必要放这里，可以需要的地方直接获取
    //@NotBlank
    //String realIp = Context.current().realIp();
}
