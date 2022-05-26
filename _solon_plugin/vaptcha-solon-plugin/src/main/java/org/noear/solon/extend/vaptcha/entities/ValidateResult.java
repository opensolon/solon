package org.noear.solon.extend.vaptcha.entities;


/**
 * @author iYarnFog
 * @since 1.5
 */
public class ValidateResult {
    /**
     * 验证结果，1为通过，0为失败
     */
    boolean success;
    /**
     * 验证得分(可信度)，区间[0, 100]
     */
    int score;
    /**
     * 验证失败时为错误信息,验证通过但是 score 有扣分时显示扣分项
     */
    String msg;


    public boolean getSuccess() {
        return success;
    }

    public int getScore() {
        return score;
    }

    public String getMsg() {
        return msg;
    }
}
