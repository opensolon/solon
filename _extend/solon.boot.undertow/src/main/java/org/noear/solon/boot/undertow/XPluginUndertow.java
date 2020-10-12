package org.noear.solon.boot.undertow;

import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.*;
import org.noear.solon.boot.undertow.http.UtHttpHandlerJsp;

/**
 * @author  by: Yukai
 * @since : 2019/3/28 15:49
 */
public class XPluginUndertow extends XPluginUndertowBase {

    @Override
    protected DeploymentManager doGenerateManager() throws Exception {
        DeploymentInfo builder = initDeploymentInfo();

        builder.addServlet(new ServletInfo("ACTServlet", UtHttpHandlerJsp.class).addMapping("/"));
        //builder.addInnerHandlerChainWrapper(h -> handler); //这个会使过滤器不能使用


        final ServletContainer container = Servlets.defaultContainer();

        DeploymentManager deploymentManager = container.addDeployment(builder);
        deploymentManager.deploy();

        return deploymentManager;

    }
}
