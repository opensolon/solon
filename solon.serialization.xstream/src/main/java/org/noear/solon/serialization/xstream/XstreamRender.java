package org.noear.solon.serialization.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import org.noear.solon.core.ModelAndView;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XRender;

import java.util.HashMap;
import java.util.Map;

//不要要入参，方便后面多视图混用
//
public class XstreamRender implements XRender {
    XStream xStream = new XStream(new StaxDriver());

    @Override
    public void render(Object obj, XContext ctx) throws Throwable {
        String txt = null;

        if(obj instanceof ModelAndView){
            txt = xStream.toXML(new HashMap<>(((Map)obj)));
        }else{
            txt = xStream.toXML(obj);
        }

        if (XPluginImp.output_meta) {
            ctx.headerSet("solon.serialization", "XstreamRender");
        }

        ctx.attrSet("output", txt);
        ctx.contentType("text/xml");
        ctx.output(txt);
    }
}
