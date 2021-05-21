package org.noear.captcha.solon.plugin.service.impl;

import org.noear.captcha.solon.plugin.model.common.RepCodeEnum;
import org.noear.captcha.solon.plugin.model.common.ResponseModel;
import org.noear.captcha.solon.plugin.model.common.StaticVariable;
import org.noear.captcha.solon.plugin.model.vo.CaptchaVO;
import org.noear.captcha.solon.plugin.service.CaptchaService;
import org.noear.captcha.solon.plugin.util.AESUtil;
import org.noear.captcha.solon.plugin.util.CacheUtil;
import org.noear.captcha.solon.plugin.util.StringUtils;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.Aop;

/**
 * @author ww
 */
@Component
public class DefaultCaptchaServiceImpl implements CaptchaService {
    /** 滑块图片 **/
    private String captchaOriginalPathJigsaw = StaticVariable.jigsaw;
    /** 点击图片 **/
    private String captchaOriginalPathClick = StaticVariable.click;
    /** AESKey **/
    private String aesKey = StaticVariable.key;

    protected static String REDIS_SECOND_CAPTCHA_KEY = "RUNNING:CAPTCHA:second-%s";

    private CaptchaService getService(String captchaType) {
        return Aop.get(captchaType.concat("CaptchaService"));
    }

    @Override
    public ResponseModel get(CaptchaVO captchaVO) {
        if (captchaVO == null) {
            return RepCodeEnum.NULL_ERROR.parseError("captchaVO");
        }
        if (StringUtils.isEmpty(captchaVO.getCaptchaType())) {
            return RepCodeEnum.NULL_ERROR.parseError("类型");
        }
        if (StaticVariable.blockPuzzle.equals(captchaVO.getCaptchaType())) {
            captchaVO.setCaptchaOriginalPath(captchaOriginalPathJigsaw);
        } else {
            captchaVO.setCaptchaOriginalPath(captchaOriginalPathClick);
        }
        return getService(captchaVO.getCaptchaType()).get(captchaVO);
    }

    @Override
    public ResponseModel check(CaptchaVO captchaVO) {
        if (captchaVO == null) {
            return RepCodeEnum.NULL_ERROR.parseError("captchaVO");
        }
        if (StringUtils.isEmpty(captchaVO.getCaptchaType())) {
            return RepCodeEnum.NULL_ERROR.parseError("类型");
        }
        if (StringUtils.isEmpty(captchaVO.getToken())) {
            return RepCodeEnum.NULL_ERROR.parseError("token");
        }
        return getService(captchaVO.getCaptchaType()).check(captchaVO);
    }

    @Override
    public ResponseModel verification(CaptchaVO captchaVO) {
        if (captchaVO == null) {
            return RepCodeEnum.NULL_ERROR.parseError("captchaVO");
        }
        if (StringUtils.isEmpty(captchaVO.getCaptchaVerification())) {
            return RepCodeEnum.NULL_ERROR.parseError("captchaVerification");
        }
        try {
            //aes解密
            String s = AESUtil.aesDecrypt(captchaVO.getCaptchaVerification(), aesKey);
            String token = s.split("---")[0];
            String pointJson = s.split("---")[1];
            //取坐标信息
            String codeKey = String.format(REDIS_SECOND_CAPTCHA_KEY, token);
            if (!CacheUtil.exists(codeKey)) {
                return ResponseModel.errorMsg(RepCodeEnum.API_CAPTCHA_INVALID);
            }
            String redisData = CacheUtil.get(codeKey);
            //二次校验取值后，即刻失效
            CacheUtil.delete(codeKey);
            if (!pointJson.equals(redisData)) {
                return ResponseModel.errorMsg(RepCodeEnum.API_CAPTCHA_COORDINATE_ERROR);
            }
        } catch (Exception e) {
            return ResponseModel.errorMsg(e.getMessage());
        }
        return ResponseModel.success();
    }

}
