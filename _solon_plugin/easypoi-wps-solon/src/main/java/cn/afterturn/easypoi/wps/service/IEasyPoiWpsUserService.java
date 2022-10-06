package cn.afterturn.easypoi.wps.service;

import cn.afterturn.easypoi.wps.entity.WpsUserEntity;

/**
 * 用户服务
 *
 * @author jueyue on 20-5-8.
 */
public interface IEasyPoiWpsUserService {

    /**
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    WpsUserEntity getUser(String userId);
}
