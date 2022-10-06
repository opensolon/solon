package cn.afterturn.easypoi.wps.entity.resreq;


import java.io.Serializable;
import java.util.*;

/**
 * @author JueYue
 * @date 2021-05-21-5-25
 * @since 1.0
 */
public class WpsConvertResponse  extends WpsResponse implements Serializable {

    // keys
    private final static String MSG_KEY = "msg";
    private final static String STATUS_KEY = "status";
    private final static String CODE_KEY = "code";
    private final static String DATA_KEY = "data";

    // msg
    private final static String SUCCESS_MSG = "ok";

    // value
    private final static String SUCCESS_VALUE = "success";

    /**
     * 请求成功,并返回请求结果集
     *
     * @param data 返回到客户端的对象
     * @return Spring mvc ResponseEntity
     */
    public static ResponseEntity<Object> success(Map<String, Object> data, String msg) {
        return getObjectResponseEntity(data, msg);
    }

    public static ResponseEntity<Object> success(String msg) {
        Map<String, Object> result = new HashMap<String, Object>() {
            {
                put(STATUS_KEY, SUCCESS_VALUE);
                put(MSG_KEY, msg);
                put(CODE_KEY, 200);
            }
        };
        return getEntity(result);
    }

    public static ResponseEntity<Object> successOk() {
        Map<String, Object> result = new HashMap<String, Object>() {
            {
                put(STATUS_KEY, SUCCESS_VALUE);
                put(MSG_KEY, SUCCESS_MSG);
                put(CODE_KEY, 200);
            }
        };
        return getEntity(result);
    }

    /**
     * 请求成功,并返回请求结果集
     *
     * @param data 返回到客户端的对象
     * @return Spring mvc ResponseEntity
     */
    public static ResponseEntity<Object> success(Map<String, Object> data) {
        return getObjectResponseEntity(data, SUCCESS_MSG);
    }

    public static ResponseEntity<Object> success(Object data) {
        Map<String, Object> result = new HashMap<String, Object>() {
            {
                put(STATUS_KEY, SUCCESS_VALUE);
                put(MSG_KEY, SUCCESS_MSG);
                put(CODE_KEY, 200);
                put(DATA_KEY, data);
            }
        };
        return getEntity(result);
    }

    public static ResponseEntity<Object> success(byte[] data, String msg) {
        Map<String, Object> result = new HashMap<String, Object>() {
            {
                put(STATUS_KEY, SUCCESS_VALUE);
                put(MSG_KEY, msg);
                put(CODE_KEY, 200);
                put(DATA_KEY, data);
            }
        };
        return getEntity(result);
    }

    public static ResponseEntity<Object> success(boolean data, String msg) {
        Map<String, Object> result = new HashMap<String, Object>() {
            {
                put(STATUS_KEY, SUCCESS_VALUE);
                put(MSG_KEY, msg);
                put(CODE_KEY, 200);
                put(DATA_KEY, data);
            }
        };
        return getEntity(result);
    }

    public static ResponseEntity<Object> bad(String msg) {
        Map<String, Object> result = new HashMap<String, Object>() {
            {
                put(STATUS_KEY, SUCCESS_VALUE);
                put(MSG_KEY, msg);
                put(CODE_KEY, 400);
            }
        };
        return getEntity(result);
    }

    private static ResponseEntity<Object> getObjectResponseEntity(Map<String, Object> data, String msg) {
        Map<String, Object> result = new HashMap<String, Object>() {
            {
                put(STATUS_KEY, SUCCESS_VALUE);
                put(MSG_KEY, msg);
                put(CODE_KEY, 200);
                for (Entry<String, Object> entry : data.entrySet()) {
                    put(entry.getKey(), entry.getValue());
                }
            }
        };
        return getEntity(result);
    }

    private static ResponseEntity<Object> getEntity(Object body) {
        List<String> contentType = new ArrayList<String>() {
            {
                add("application/json;charset=utf-8");
            }
        };
        Map<String, Object> headers = new HashMap<String, Object>() {
            {
                put("Content-Type", contentType);
            }
        };
        return new ResponseEntity<>(body, headers, 200);
    }
}
