package me.chanjar.weixin.qidian.solon.properties;

import static me.chanjar.weixin.qidian.solon.enums.StorageType.Memory;

import java.io.Serializable;

import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import me.chanjar.weixin.qidian.solon.enums.HttpClientType;
import me.chanjar.weixin.qidian.solon.enums.StorageType;

import lombok.Data;

/**
 * 企点接入相关配置属性.
 *
 * @author someone
 */
@Data
@Configuration
@Inject("${wx.qidian}")
public class WxQidianProperties {

  /**
   * 设置腾讯企点的appid.
   */
  private String appId;

  /**
   * 设置腾讯企点的app secret.
   */
  private String secret;

  /**
   * 设置腾讯企点的token.
   */
  private String token;

  /**
   * 设置腾讯企点的EncodingAESKey.
   */
  private String aesKey;

  /**
   * 自定义host配置
   */
  private HostConfig hosts;

  /**
   * 存储策略
   */
  private ConfigStorage configStorage = new ConfigStorage();

  @Data
  public static class ConfigStorage implements Serializable {
    private static final long serialVersionUID = 4815731027000065434L;

    /**
     * 存储类型.
     */
    private StorageType type = Memory;

    /**
     * 指定key前缀.
     */
    private String keyPrefix = "wx";

    /**
     * redis连接配置.
     */
    private RedisProperties redis = new RedisProperties();

    /**
     * http客户端类型.
     */
    private HttpClientType httpClientType = HttpClientType.HttpClient;

    /**
     * http代理主机.
     */
    private String httpProxyHost;

    /**
     * http代理端口.
     */
    private Integer httpProxyPort;

    /**
     * http代理用户名.
     */
    private String httpProxyUsername;

    /**
     * http代理密码.
     */
    private String httpProxyPassword;

  }

}
