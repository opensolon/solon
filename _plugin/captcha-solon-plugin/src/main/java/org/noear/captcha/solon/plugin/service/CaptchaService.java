package org.noear.captcha.solon.plugin.service;

import org.noear.captcha.solon.plugin.model.common.ResponseModel;
import org.noear.captcha.solon.plugin.model.vo.CaptchaVO;

/**
 * @Title: 验证码缓存接口
 * @author ww
 */
public interface CaptchaService {

    /**
     * 获取验证码
     * @param captchaVO
     * @return
     */
    ResponseModel get(CaptchaVO captchaVO);

    /**
     * 核对验证码(前端)
     * @param captchaVO
     * @return
     */
    ResponseModel check(CaptchaVO captchaVO);

    /**
     * 二次校验验证码(后端)
     * @param captchaVO
     * @return
     */
    ResponseModel verification(CaptchaVO captchaVO);


}
