package org.noear.solon.extend.captcha.integration;

import com.anji.captcha.service.CaptchaService;
import com.anji.captcha.service.impl.CaptchaServiceFactory;
import com.anji.captcha.util.Base64Utils;
import com.anji.captcha.util.FileCopyUtils;
import com.anji.captcha.util.ImageUtils;
import com.anji.captcha.util.StringUtils;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author noear
 * @since 1.5
 */
@Configuration
public class AjCaptchaServiceConfiguration {

    public AjCaptchaServiceConfiguration() {
    }

    @Bean
    public CaptchaService captchaService(AjCaptchaProperties prop) {
        //logger.info("自定义配置项：{}", prop.toString());
        Properties config = new Properties();
        config.put("captcha.cacheType", prop.getCacheType().name());
        config.put("captcha.water.mark", prop.getWaterMark());
        config.put("captcha.font.type", prop.getFontType());
        config.put("captcha.type", prop.getType().getCodeValue());
        config.put("captcha.interference.options", prop.getInterferenceOptions());
        config.put("captcha.captchaOriginalPath.jigsaw", prop.getJigsaw());
        config.put("captcha.captchaOriginalPath.pic-click", prop.getPicClick());
        config.put("captcha.slip.offset", prop.getSlipOffset());
        config.put("captcha.aes.status", String.valueOf(prop.getAesStatus()));
        config.put("captcha.water.font", prop.getWaterFont());
        config.put("captcha.cache.number", prop.getCacheNumber());
        config.put("captcha.timing.clear", prop.getTimingClear());
        config.put("captcha.history.data.clear.enable", prop.isHistoryDataClearEnable() ? "1" : "0");
        config.put("captcha.req.frequency.limit.enable", prop.getReqFrequencyLimitEnable() ? "1" : "0");
        config.put("captcha.req.get.lock.limit", prop.getReqGetLockLimit() + "");
        config.put("captcha.req.get.lock.seconds", prop.getReqGetLockSeconds() + "");
        config.put("captcha.req.get.minute.limit", prop.getReqGetMinuteLimit() + "");
        config.put("captcha.req.check.minute.limit", prop.getReqCheckMinuteLimit() + "");
        config.put("captcha.req.verify.minute.limit", prop.getReqVerifyMinuteLimit() + "");
        if (StringUtils.isNotBlank(prop.getJigsaw()) && prop.getJigsaw().startsWith("classpath:") || StringUtils.isNotBlank(prop.getPicClick()) && prop.getPicClick().startsWith("classpath:")) {
            config.put("captcha.init.original", "true");
            initializeBaseMap(prop.getJigsaw(), prop.getPicClick());
        }

        CaptchaService s = CaptchaServiceFactory.getInstance(config);
        return s;
    }

    private static void initializeBaseMap(String jigsaw, String picClick) {
        Map<String, String> originalMap = getResourcesImagesFile(jigsaw + "/original/*.png");
        Map<String, String> slidingBlockMap = getResourcesImagesFile(jigsaw + "/slidingBlock/*.png");
        Map<String, String> picClickMap = getResourcesImagesFile(picClick + "/*.png");

        ImageUtils.cacheBootImage(originalMap, slidingBlockMap, picClickMap);
    }

    public static Map<String, String> getResourcesImagesFile(String path) {
        Map<String, String> imgMap = new HashMap();

        //todo: 这里可能会有问题
        try {
            Enumeration<URL> resources = Utils.getResources(path);

            while (resources.hasMoreElements()){
                URL resource = resources.nextElement();
                byte[] bytes = FileCopyUtils.copyToByteArray(resource.openStream());
                String string = Base64Utils.encodeToString(bytes);
                String filename = new File(resource.getFile()).getName();
                imgMap.put(filename, string);
            }
        } catch (Exception var11) {
            var11.printStackTrace();
        }

        return imgMap;
    }
}
