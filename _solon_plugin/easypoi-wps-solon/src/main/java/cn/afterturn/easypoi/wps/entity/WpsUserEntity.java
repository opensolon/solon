package cn.afterturn.easypoi.wps.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jueyue on 20-5-7.
 */
@Data
public class WpsUserEntity  implements Serializable {

    /**
     * 用户id，长度小于40
     */
    private String id         = "0";
    /**
     * 用户名称
     */
    private String name       = "悟耘-EasyPoi";
    /**
     * 用户操作权限，write：可编辑，read：预览
     */
    private String permission = "read";
    /**
     * 用户头像地址
     */
    private String avatar_url = "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=50688014,2596832035&fm=11&gp=0.jpg";
}
