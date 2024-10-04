package org.noear.solon.web.webservices.integration;

import org.noear.solon.Solon;
import org.noear.solon.Utils;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.Set;

/**
 * @author noear
 * @since 1.0
 */
public class WebServiceContainerInitializerImpl implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
        //@WebServlet(name = "ws", urlPatterns = "/ws/*", loadOnStartup = 0)
        String path = Solon.cfg().get("server.webservices.path");

        if (Utils.isEmpty(path)) {
            path = "/ws/*";
        } else {
            if (path.startsWith("/") == false) {
                path = "/" + path;
            }

            if (path.endsWith("/")) {
                path = path + "*";
            } else {
                path = path + "/*";
            }
        }

        ServletRegistration.Dynamic registration = servletContext.addServlet("WebServiceServlet", WebServiceServlet.class);
        if (registration != null) {
            registration.setLoadOnStartup(0);
            registration.addMapping(path);
        }
    }
}
