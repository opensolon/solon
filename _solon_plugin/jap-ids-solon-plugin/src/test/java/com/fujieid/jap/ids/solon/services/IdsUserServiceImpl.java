package com.fujieid.jap.ids.solon.services;

import com.fujieid.jap.ids.model.UserInfo;
import com.fujieid.jap.ids.service.IdsUserService;
import org.noear.solon.annotation.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * @author yadong.zhang (yadong.zhang0415(a)gmail.com)
 * @version 1.0.0
 * @date 2021-04-14 10:27
 * @since 1.0.0
 */
public class IdsUserServiceImpl implements IdsUserService {
    public List<UserInfo> userInfoList = new LinkedList<>();

    public IdsUserServiceImpl() {
        for (int i = 0; i < 10; i++) {
            UserInfo userInfo = new UserInfo();
            userInfoList.add(userInfo
                    .setId(i + "")
                    .setUsername("test")
                    .setName("justauthplus")
                    .setNickname("jap")
                    .setBirthdate("2021-01-12")
                    .setWebsite("https://gitee.com/fujieid/jap")
                    .setProfile("https://justauth.plus/")
            );
        }
    }

    /**
     * Login with account and password
     *
     * @param username account number
     * @param password password
     * @return UserInfo
     */
    @Override
    public UserInfo loginByUsernameAndPassword(String username, String password, String clientId) {
        return userInfoList.stream().filter(userInfo -> userInfo.getUsername().equals(username)).findFirst().orElse(null);
    }

    /**
     * Get user info by userid.
     *
     * @param userId userId of the business system
     * @return UserInfo
     */
    @Override
    public UserInfo getById(String userId) {
        return userInfoList.stream().filter(userInfo -> userInfo.getId().equals(userId)).findFirst().orElse(null);
    }

    /**
     * Get user info by username.
     *
     * @param username username of the business system
     * @return UserInfo
     */
    @Override
    public UserInfo getByName(String username, String clientId) {
        return userInfoList.stream().filter(userInfo -> userInfo.getUsername().equals(username)).findFirst().orElse(null);
    }
}
