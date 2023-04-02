package com.github.binarywang.wxpay.solon.config;

import org.apache.commons.lang3.StringUtils;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import com.github.binarywang.wxpay.solon.properties.WxPayProperties;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;

/**
 * <pre>
 *  微信支付自动配置
 *  Created by BinaryWang on 2019/4/17.
 * </pre>
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Configuration
@Condition(onProperty = "${wx.pay:enabled}=enabled",onClass = WxPayService.class)
public class WxPayAutoConfiguration {

  /**
   * 构造微信支付服务对象.
   *
   * @return 微信支付service
   */
  @Bean
  @Condition(onMissingBean = WxPayService.class)
  public WxPayService wxPayService(WxPayProperties properties) {
    final WxPayServiceImpl wxPayService = new WxPayServiceImpl();
    WxPayConfig payConfig = new WxPayConfig();
    payConfig.setAppId(StringUtils.trimToNull(properties.getAppId()));
    payConfig.setMchId(StringUtils.trimToNull(properties.getMchId()));
    payConfig.setMchKey(StringUtils.trimToNull(properties.getMchKey()));
    payConfig.setSubAppId(StringUtils.trimToNull(properties.getSubAppId()));
    payConfig.setSubMchId(StringUtils.trimToNull(properties.getSubMchId()));
    payConfig.setKeyPath(StringUtils.trimToNull(properties.getKeyPath()));
    //以下是apiv3以及支付分相关
    payConfig.setServiceId(StringUtils.trimToNull(properties.getServiceId()));
    payConfig.setPayScoreNotifyUrl(StringUtils.trimToNull(properties.getPayScoreNotifyUrl()));
    payConfig.setPrivateKeyPath(StringUtils.trimToNull(properties.getPrivateKeyPath()));
    payConfig.setPrivateCertPath(StringUtils.trimToNull(properties.getPrivateCertPath()));
    payConfig.setCertSerialNo(StringUtils.trimToNull(properties.getCertSerialNo()));
    payConfig.setApiV3Key(StringUtils.trimToNull(properties.getApiv3Key()));

    wxPayService.setConfig(payConfig);
    return wxPayService;
  }

}
