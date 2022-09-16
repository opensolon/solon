package com.fujieid.jap.ids.solon.http.controller;

import com.fujieid.jap.http.adapter.jakarta.JakartaRequestAdapter;
import com.fujieid.jap.http.adapter.jakarta.JakartaResponseAdapter;
import com.fujieid.jap.ids.endpoint.ErrorEndpoint;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 颖
 * @since 1.6
 */
public class ErrorController extends IdsController {
    private final ErrorEndpoint errorEndpoint = new ErrorEndpoint();

    @Get
    @Mapping("error")
    public void error(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.errorEndpoint.showErrorPage(
                new JakartaRequestAdapter(request),
                new JakartaResponseAdapter(response)
        );
    }
}
