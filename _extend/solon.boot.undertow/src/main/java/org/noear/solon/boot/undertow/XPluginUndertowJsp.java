package org.noear.solon.boot.undertow;

import io.undertow.jsp.HackInstanceManager;
import io.undertow.jsp.JspServletBuilder;
import io.undertow.servlet.api.*;
import org.apache.jasper.deploy.JspPropertyGroup;
import org.apache.jasper.deploy.TagLibraryInfo;
import org.noear.solon.boot.undertow.http.UtHttpHandlerJsp;
import org.noear.solon.boot.undertow.jsp.JspResourceManager;
import org.noear.solon.boot.undertow.jsp.JspServletEx;
import org.noear.solon.boot.undertow.jsp.JspTldLocator;
import org.noear.solon.core.XClassLoader;

import java.util.HashMap;

/**
 * @author by: Yukai
 * @since: 2019/3/28 15:50
 */
public class XPluginUndertowJsp extends XPluginUndertowBase {

    @Override
    protected DeploymentManager doGenerateManager() throws Exception{
        DeploymentInfo builder = initDeploymentInfo();

        //添加jsp处理
        String fileRoot = getResourceRoot();
        builder.setResourceManager(new JspResourceManager(XClassLoader.global(), fileRoot))
                .addServlet(new ServletInfo("ACTServlet", UtHttpHandlerJsp.class).addMapping("/"))
                .addServlet(JspServletEx.createServlet("JSPServlet", "*.jsp"));


        //添加taglib支持
        HashMap<String, TagLibraryInfo> tagLibraryMap = JspTldLocator.createTldInfos("WEB-INF");
        JspServletBuilder.setupDeployment(builder, new HashMap<String, JspPropertyGroup>(), tagLibraryMap, new HackInstanceManager());



        //开始部署
        final ServletContainer container = ServletContainer.Factory.newInstance();
        DeploymentManager deploymentManager = container.addDeployment(builder);
        deploymentManager.deploy();

        return deploymentManager;
    }
}
