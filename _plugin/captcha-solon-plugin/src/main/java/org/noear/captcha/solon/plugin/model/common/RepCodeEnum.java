package org.noear.captcha.solon.plugin.model.common;

import java.text.MessageFormat;

/**
 * 返回应答码
 * @author ww
 *
 */
public enum RepCodeEnum {

    /** 0001 - 0099 网关应答码 */
    SUCCESS("0000", "成功"),
    ERROR("0001", "操作失败"),
    EXCEPTION("9999", "服务器内部异常"),

    BLANK_ERROR("0011", "{0}不能为空"),
    NULL_ERROR("0011", "{0}不能为空"),
    NOT_NULL_ERROR("0012", "{0}必须为空"),
    NOT_EXIST_ERROR("0013", "{0}数据库中不存在"),
    EXIST_ERROR("0014", "{0}数据库中已存在"),
    PARAM_TYPE_ERROR("0015", "{0}类型错误"),
    PARAM_FORMAT_ERROR("0016", "{0}格式错误"),

    API_CAPTCHA_INVALID("6110", "验证码已失效，请重新获取"),
    API_CAPTCHA_COORDINATE_ERROR("6111", "验证失败"),
    API_CAPTCHA_ERROR("6112", "获取验证码失败,请联系管理员"),

    ;
    private String code;
    private String desc;

    RepCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public String getCode() {
        return code;
    }
    public String getDesc() {
        return desc;
    }
    public String getName(){
        return this.name();
    }

    /** 将入参fieldNames与this.desc组合成错误信息
     *  {fieldName}不能为空
     * @param fieldNames
     * @return
     */
    public ResponseModel parseError(Object... fieldNames) {
        ResponseModel errorMessage=new ResponseModel();
        String newDesc = MessageFormat.format(this.desc, fieldNames);

        errorMessage.setRepCode(this.code);
        errorMessage.setRepMsg(newDesc);
        return errorMessage;
    }

}
