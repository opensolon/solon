package org.noear.captcha.solon.plugin;

import org.noear.captcha.solon.plugin.model.common.StaticVariable;
import org.noear.captcha.solon.plugin.service.CaptchaService;
import org.noear.captcha.solon.plugin.service.impl.BlockPuzzleCaptchaServiceImpl;
import org.noear.captcha.solon.plugin.service.impl.ClickWordCaptchaServiceImpl;
import org.noear.captcha.solon.plugin.service.impl.DefaultCaptchaServiceImpl;
import org.noear.captcha.solon.plugin.util.ImageUtils;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.cache.CacheService;


/**
 * Solon 插件接口实现，完成对接与注入支持
 *
 * @author noear
 * @since 2020-09-01
 * */
public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        app.beanMake(DefaultCaptchaServiceImpl.class);
        app.beanMake(BlockPuzzleCaptchaServiceImpl.class);
        app.beanMake(ClickWordCaptchaServiceImpl.class);
        //初始化底图
        ImageUtils.cacheImage(StaticVariable.jigsaw, StaticVariable.click);
    }
}
