package org.noear.captcha.solon.plugin.model.common;

import org.noear.captcha.solon.plugin.util.StringUtils;
import org.noear.solon.Solon;

/**
 * @author ww
 */
public class StaticVariable {
    /** 滑块图片 **/
    public static String jigsaw = StringUtils.isNotBlank(Solon.cfg().get("captcha.captchaOriginalPath.jigsaw"))?Solon.cfg().get("captcha.captchaOriginalPath.jigsaw"):"images/jigsaw";
    /** 点击图片 **/
    public static String click = StringUtils.isNotBlank(Solon.cfg().get("captcha.captchaOriginalPath.pic-click"))?Solon.cfg().get("captcha.captchaOriginalPath.pic-click"):"images/pic-click";
    /** aes密钥 **/
    public static String key = StringUtils.isNotBlank(Solon.cfg().get("captcha.aes.key"))?Solon.cfg().get("captcha.aes.key"):"XYCPOiNNq2a05jvx";

    public static String blockPuzzle = "blockPuzzle";

    /** 文字验证码字体 **/
    public static String fontType = StringUtils.isNotBlank(Solon.cfg().get("captcha.font.type"))?Solon.cfg().get("captcha.font.type"):"宋体";

    /** 水印字体 **/
    public static String waterMarkFont = StringUtils.isNotBlank(Solon.cfg().get("captcha.water.font"))?Solon.cfg().get("captcha.water.font"):"宋体";

    /** 水印 **/
    public static String waterMark = Solon.cfg().get("captcha.water.mark");

    /** 偏移量 **/
    public static String slipOffset = StringUtils.isNotBlank(Solon.cfg().get("captcha.slip.offset"))?Solon.cfg().get("captcha.slip.offset"):"5";
}
