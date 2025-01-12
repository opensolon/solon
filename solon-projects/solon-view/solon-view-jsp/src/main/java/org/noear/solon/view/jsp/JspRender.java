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
package org.noear.solon.view.jsp;

import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Render;
import org.noear.solon.view.ViewConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JspRender implements Render {
    @Override
    public void render(Object obj, Context ctx) throws Throwable {
        if(obj == null){
            return;
        }

        if (obj instanceof ModelAndView) {
            render_mav((ModelAndView) obj, ctx);
        }else{
            ctx.output(obj.toString());
        }
    }

    public void render_mav(ModelAndView mv, Context ctx) throws Throwable {
        if(ctx.contentTypeNew() == null) {
            ctx.contentType("text/html;charset=utf-8");
        }

        if (ViewConfig.isOutputMeta()) {
            ctx.headerSet(ViewConfig.HEADER_VIEW_META, "JspRender");
        }

        //添加 context 变量
        mv.putIfAbsent("context", ctx);

        HttpServletResponse response = (HttpServletResponse)ctx.response();
        HttpServletRequest request = (HttpServletRequest)ctx.request();

        mv.model().forEach(request::setAttribute);

        String view = mv.view();

        if (view.endsWith(".jsp") == true) {

            if (view.startsWith("/") == true) {
                view = ViewConfig.getViewPrefix() + view;
            } else {
                view = ViewConfig.getViewPrefix() + "/" + view;
            }
            view = view.replace("//", "/");
        }

        request.getServletContext()
               .getRequestDispatcher(view)
               .forward(request, response);
    }
}
