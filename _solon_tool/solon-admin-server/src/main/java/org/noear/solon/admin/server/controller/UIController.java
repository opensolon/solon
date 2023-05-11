package org.noear.solon.admin.server.controller;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Produces;
import org.noear.solon.boot.web.MimeType;
import org.noear.solon.core.handle.Context;
import org.noear.solon.web.staticfiles.StaticMimes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Controller
public class UIController {

    @Get
    @Mapping("/")
    @Produces(MimeType.TEXT_HTML_VALUE)
    public InputStream index() throws IOException {
        InputStream stream = this.getClass().getResourceAsStream("/META-INF/solon-admin-server-ui/index.html");
        if (stream == null)
            throw new IOException("Could not find index.html from solon-admin-server-ui, please check if the solon-admin-server-ui dependency is added");
        return stream;
    }

    @Get
    @Mapping("/**")
    public void resources(Context ctx) throws IOException {
        if (ctx.path().equals("/index.html")) {
            ctx.redirect("/");
            return;
        }

        InputStream stream = this.getClass().getResourceAsStream("/META-INF/solon-admin-server-ui" + ctx.path());
        if (stream == null) {
            ctx.status(404);
            return;
        }

        Optional.ofNullable(StaticMimes.findByFileName(ctx.path())).ifPresent(ctx::contentType);

        ctx.output(stream);
    }

}
