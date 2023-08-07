package org.noear.solon.admin.server.data;

import lombok.Data;

import java.io.Serializable;

/**
 * @author songyinyin
 * @since 2023/8/7 19:01
 */
@Data
public class AdminResponse implements Serializable {

    /**
     * 状态码，0 表示成功，其他表示失败
     */
    private int code = -1;

    private String message;

    public static AdminResponse success() {
        AdminResponse response = new AdminResponse();
        response.setCode(0);
        return response;
    }

    public static AdminResponse success(String message) {
        AdminResponse response = new AdminResponse();
        response.setCode(0);
        response.setMessage(message);
        return response;
    }

    /**
     * 判断本次操作是否成功
     */
    public boolean isSuccess() {
        return code == 0;
    }
}
