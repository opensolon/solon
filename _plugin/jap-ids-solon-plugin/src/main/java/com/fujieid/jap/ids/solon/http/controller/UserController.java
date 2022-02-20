package com.fujieid.jap.ids.solon.http.controller;

import com.fujieid.jap.http.adapter.jakarta.JakartaRequestAdapter;
import com.fujieid.jap.ids.endpoint.UserInfoEndpoint;
import com.fujieid.jap.ids.model.IdsResponse;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author 颖
 */
public class UserController extends IdsController {

    private final UserInfoEndpoint userInfoEndpoint = new UserInfoEndpoint();

    @Get
    @Mapping("userInfo")
    public Map<String, Object> userInfo(HttpServletRequest request) {
        return this.userInfoEndpoint
                .getCurrentUserInfo(new JakartaRequestAdapter(request));
    }

}
