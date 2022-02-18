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
 */
public class CheckSessionController extends BaseController {
    @Get
    @Mapping("check_session")
    public Map<String, Object> check_session(HttpServletRequest request) {
        IdsResponse<String, Object> response = new IdsResponse<String, Object>()
                .data(JapIds.isAuthenticated(new JakartaRequestAdapter(request)));

        return response;
    }
}
