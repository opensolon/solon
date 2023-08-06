package cn.afterturn.easypoi.wps.entity;

import cn.afterturn.easypoi.wps.entity.resreq.WpsResponse;
import lombok.Data;

import java.io.Serializable;

/**
 * @author JueYue
 * 界面的Token数据,默认不使用
 */
@Data
public class WpsToken extends WpsResponse implements Serializable {

    private int    expires_in;
    private String token;
    private String wpsUrl;

}
