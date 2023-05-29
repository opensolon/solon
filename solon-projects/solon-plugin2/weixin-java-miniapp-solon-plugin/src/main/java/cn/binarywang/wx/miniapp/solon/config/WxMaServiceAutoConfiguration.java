package cn.binarywang.wx.miniapp.solon.config;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import cn.binarywang.wx.miniapp.solon.enums.HttpClientType;
import cn.binarywang.wx.miniapp.solon.properties.WxMaProperties;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceHttpClientImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceJoddHttpImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceOkHttpImpl;
import cn.binarywang.wx.miniapp.config.WxMaConfig;

/**
 * 微信小程序平台相关服务自动注册.
 *
 * @author someone TaoYu
 */
@Configuration
public class WxMaServiceAutoConfiguration {

  /**
   * 小程序service.
   *
   * @return 小程序service
   */
  @Bean
  @Condition(onMissingBean =  WxMaService.class)
  public WxMaService wxMaService(WxMaConfig wxMaConfig,WxMaProperties wxMaProperties) {
    HttpClientType httpClientType = wxMaProperties.getConfigStorage().getHttpClientType();
    WxMaService wxMaService;
    switch (httpClientType) {
      case OkHttp:
        wxMaService = new WxMaServiceOkHttpImpl();
        break;
      case JoddHttp:
        wxMaService = new WxMaServiceJoddHttpImpl();
        break;
      case HttpClient:
        wxMaService = new WxMaServiceHttpClientImpl();
        break;
      default:
        wxMaService = new WxMaServiceImpl();
        break;
    }
    wxMaService.setWxMaConfig(wxMaConfig);
    return wxMaService;
  }
}
