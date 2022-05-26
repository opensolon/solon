package com.fujieid.jap.ids.solon.http.controller;

import com.fujieid.jap.http.adapter.jakarta.JakartaRequestAdapter;
import com.fujieid.jap.http.adapter.jakarta.JakartaResponseAdapter;
import com.fujieid.jap.ids.endpoint.ApprovalEndpoint;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 颖
 * @since 1.6
 */
public class ApprovalController extends IdsController {

    private final ApprovalEndpoint approvalEndpoint = new ApprovalEndpoint();

    @Get
    @Mapping("confirm")
    public void confirm(HttpServletRequest request, HttpServletResponse response) throws IOException {
        approvalEndpoint.showConfirmPage(
                new JakartaRequestAdapter(request),
                new JakartaResponseAdapter(response)
        );
    }
}
