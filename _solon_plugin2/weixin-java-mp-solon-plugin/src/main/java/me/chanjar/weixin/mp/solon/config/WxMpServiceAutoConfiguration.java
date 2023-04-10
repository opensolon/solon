package me.chanjar.weixin.mp.solon.config;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import me.chanjar.weixin.mp.solon.enums.HttpClientType;
import me.chanjar.weixin.mp.solon.properties.WxMpProperties;

import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceHttpClientImpl;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.api.impl.WxMpServiceJoddHttpImpl;
import me.chanjar.weixin.mp.api.impl.WxMpServiceOkHttpImpl;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;

/**
 * 微信公众号相关服务自动注册.
 *
 * @author someone
 */
@Configuration
public class WxMpServiceAutoConfiguration {

  @Bean
  @Condition(onMissingBean = WxMpService.class)
  public WxMpService wxMpService(WxMpConfigStorage configStorage, WxMpProperties wxMpProperties) {
    HttpClientType httpClientType = wxMpProperties.getConfigStorage().getHttpClientType();
    WxMpService wxMpService;
    switch (httpClientType) {
      case OkHttp:
        wxMpService = newWxMpServiceOkHttpImpl();
        break;
      case JoddHttp:
        wxMpService = newWxMpServiceJoddHttpImpl();
        break;
      case HttpClient:
        wxMpService = newWxMpServiceHttpClientImpl();
        break;
      default:
        wxMpService = newWxMpServiceImpl();
        break;
    }

    wxMpService.setWxMpConfigStorage(configStorage);
    return wxMpService;
  }

  private WxMpService newWxMpServiceImpl() {
    return new WxMpServiceImpl();
  }

  private WxMpService newWxMpServiceHttpClientImpl() {
    return new WxMpServiceHttpClientImpl();
  }

  private WxMpService newWxMpServiceOkHttpImpl() {
    return new WxMpServiceOkHttpImpl();
  }

  private WxMpService newWxMpServiceJoddHttpImpl() {
    return new WxMpServiceJoddHttpImpl();
  }

}
