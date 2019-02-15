package org.noear.solon.extend.rockuapi;

import lib.sponge.rock.RockClient;
import lib.sponge.rock.RockUtil;
import noear.water.utils.TextUtil;

import java.sql.SQLException;

public class RockCode {
    /** 失败，未知错误 */
    public static final int CODE_0=0;
    /** 成功 */
    public static final int CODE_1=1;

    /** 请求的接口不存在或不再支持 */
    public static final int CODE_11=11;
    /** 请求的签名校验失败 */
    public static final int CODE_12=12;
    /** 请求的参数缺少或有错误 */
    public static final int CODE_13=13;
    /** 请求的不符合规范 */
    public static final int CODE_14=14;


    public static final String CODE_txt(int agroup_id,RockApiResult result) throws SQLException {
        if (agroup_id > 0) {
            String message = RockClient.getAppCode(agroup_id, result.code);

            if (TextUtil.isEmpty(message) == false) {
                return message;
            }
        }

        switch (result.code) {
            case 1:
                return "Succeed";
            case 11:
                return "The api not exist";
            case 12:
                return "The signature error";
            case 13:
                return "Parameter missing or error" + (result.message == null ? "" : "(" + result.message + ")");
            case 14:
                return "The request is not up to par";
            default:
                return "Unknown error!";
        }
    }

}
