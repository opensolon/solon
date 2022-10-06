package cn.afterturn.easypoi.wps.service;

/**
 * wps 基础服务接口
 */
public interface IEasyPoiWpsService {

    /**
     * APP秘钥
     *
     * @return
     */
    String getAppSecret();

    /**
     * 获取WPS配置的AppId
     *
     * @return
     */
    String getAppId();
}
