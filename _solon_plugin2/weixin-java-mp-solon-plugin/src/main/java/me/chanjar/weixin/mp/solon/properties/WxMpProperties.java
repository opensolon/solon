package me.chanjar.weixin.mp.solon.properties;

import static me.chanjar.weixin.mp.solon.enums.StorageType.Memory;

import java.io.Serializable;

import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import me.chanjar.weixin.mp.solon.enums.HttpClientType;
import me.chanjar.weixin.mp.solon.enums.StorageType;

import lombok.Data;

/**
 * 微信接入相关配置属性.
 *
 * @author someone
 */
@Data
@Inject("${wx.mp}")
@Configuration
public class WxMpProperties {

  /**
   * 设置微信公众号的appid.
   */
  private String appId;

  /**
   * 设置微信公众号的app secret.
   */
  private String secret;

  /**
   * 设置微信公众号的token.
   */
  private String token;

  /**
   * 设置微信公众号的EncodingAESKey.
   */
  private String aesKey;

  /**
   * 自定义host配置
   */
  private HostConfig hosts;

  /**
   * 存储策略
   */
  private final ConfigStorage configStorage = new ConfigStorage();

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
    private final RedisProperties redis = new RedisProperties();

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
