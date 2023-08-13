package cn.afterturn.easypoi.wps.entity.resreq;

import java.io.Serializable;

/**
 * @author jueyue on 20-5-8.
 * @author noear
 */
public class WpsResponse implements Serializable {

    private int code = 200;

    private String msg;
    private String status = "success";

    public int getCode(){
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg(){
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static WpsResponse success() {
        return new WpsResponse();
    }
}
