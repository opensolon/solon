package cn.afterturn.easypoi.wps.entity.resreq;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jueyue on 20-5-8.
 */
@Data
public class WpsResponse implements Serializable {

    private int code = 200;

    private String msg;
    private String status = "success";

    public static WpsResponse success() {
        return new WpsResponse();
    }
}
