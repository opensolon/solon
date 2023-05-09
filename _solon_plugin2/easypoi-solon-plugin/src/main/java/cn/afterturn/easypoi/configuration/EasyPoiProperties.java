package cn.afterturn.easypoi.configuration;


import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * Created by xfworld on 2017-11-22.
 **/
@Inject(value = "${easy.poi.base}",required = false)
@Configuration
public class EasyPoiProperties {
    /**
     * 是否是开发模式，开发模式不缓存
     */
    private boolean isDev = false;

}
