package com.fujieid.jap.ids.solon.http.controller;

import com.fujieid.jap.http.adapter.jakarta.JakartaRequestAdapter;
import com.fujieid.jap.ids.JapIds;
import com.fujieid.jap.ids.model.IdsResponse;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author 颖
 * @since 1.6
 */
public class CheckSessionController extends IdsController {
    @Get
    @Mapping("check_session")
    public Map<String, Object> checkSession(HttpServletRequest request) {
        return new IdsResponse<String, Object>()
                .data(JapIds.isAuthenticated(new JakartaRequestAdapter(request)));
    }
}
