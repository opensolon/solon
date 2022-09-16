package demo;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;

/**
 * @author noear 2022/2/24 created
 */
@Controller
public class LoginController {
    @Inject
    private CaptchaService captchaService;

    @Post
    @Mapping("/login")
    public ResponseModel get(CaptchaVO captchaVO) {
        //必传参数：captchaVO.captchaVerification
        ResponseModel response = captchaService.verification(captchaVO);
        if (response.isSuccess() == false) {
            //验证码校验失败，返回信息告诉前端
            //repCode  0000  无异常，代表成功
            //repCode  9999  服务器内部异常
            //repCode  0011  参数不能为空
            //repCode  6110  验证码已失效，请重新获取
            //repCode  6111  验证失败
            //repCode  6112  获取验证码失败,请联系管理员
            //repCode  6113  底图未初始化成功，请检查路径
            //repCode  6201  get接口请求次数超限，请稍后再试!
            //repCode  6206  无效请求，请重新获取验证码
            //repCode  6202  接口验证失败数过多，请稍后再试
            //repCode  6204  check接口请求次数超限，请稍后再试!
        }
        return response;
    }
}