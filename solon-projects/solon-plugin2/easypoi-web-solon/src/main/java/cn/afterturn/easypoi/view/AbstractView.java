package cn.afterturn.easypoi.view;

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.Render;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author noear 2022/10/7 created
 */
public abstract   class AbstractView  {
    String contentType;

    protected abstract void renderOutputModel(Map<String, Object> model, Context ctx) throws Throwable;

    protected boolean generatesDownloadContent() {
        return false;
    }

    protected ByteArrayOutputStream createTemporaryOutputStream() {
        return new ByteArrayOutputStream(4096);
    }

    protected void writeToResponse(Context ctx, ByteArrayOutputStream baos) throws IOException {
        ctx.contentType(this.getContentType());
        ctx.contentLength(baos.size());
        OutputStream out = ctx.outputStream();
        baos.writeTo(out);
        out.flush();
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return this.contentType;
    }


}
