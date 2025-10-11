/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.boot.jetty.http;

import org.eclipse.jetty.util.component.LifeCycle;
import org.noear.solon.web.servlet.SolonServletInstaller;

import javax.servlet.*;
import java.util.*;

public class JtContainerInitializer implements LifeCycle.Listener {
    SolonServletInstaller initializer;
    ServletContext sc;

    public JtContainerInitializer(ServletContext servletContext) {
        this.sc = servletContext;
        this.initializer = new SolonServletInstaller();
    }

    @Override
    public void lifeCycleStarting(LifeCycle event) {
        try {
            initializer.startup(new HashSet<Class<?>>(), sc);
        } catch (ServletException ex) {
            throw new RuntimeException(ex);
        }
    }
}
