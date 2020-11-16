package org.noear.solon.extend.staticfiles;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.handler.Context;
import org.noear.solon.core.handler.Handler;
import org.noear.solon.core.handler.MethodType;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.regex.Pattern;

class StaticResourceHandler implements Handler {
    private static final String CACHE_CONTROL = "Cache-Control";
    private static final String LAST_MODIFIED = "Last-Modified";

    private String _debugBaseUri;
    private String _baseUri;
    private StaticFiles staticFiles =  StaticFiles.instance();
    private Pattern _rule;

    public StaticResourceHandler(String baseUri) {
        _baseUri = baseUri;

        if(Solon.cfg().isDebugMode()){
            String dirroot = Utils.getResource("/").toString().replace("target/classes/", "");
            if(dirroot.startsWith("file:")) {
                _debugBaseUri = dirroot + "src/main/resources" + _baseUri;
            }
        }

        String expr = "(" + String.join("|", staticFiles.keySet()) + ")$";

        _rule = Pattern.compile(expr, Pattern.CASE_INSENSITIVE);
    }

    public URL getResource(String path) throws Exception{
        if(_debugBaseUri != null){
            URI uri = URI.create(_debugBaseUri+path);
            File file = new File(uri);

            if(file.exists()){
                return uri.toURL();
            }else{
                return null;
            }
        }else{
            return Utils.getResource(_baseUri + path);
        }
    }

    @Override
    public void handle(Context context) throws Exception {
        if (context.getHandled()) {
            return;
        }

        if(MethodType.GET.name.equals( context.method()) == false){
            return;
        }

        if(_rule.matcher(context.path()).find()==false){
            return;
        }

        String path = context.path();

        URL uri = getResource(path);

        if (uri == null) {
            return;
        } else {
            context.setHandled(true);

            String modified_since = context.header("If-Modified-Since");
            String modified_now = modified_time.toString();

            if (modified_since != null) {
                if (modified_since.equals(modified_now)) {
                    context.headerSet(CACHE_CONTROL, "max-age=6000");//单位秒
                    context.headerSet(LAST_MODIFIED, modified_now);
                    context.status(304);
                    return;
                }
            }

            int idx = path.lastIndexOf(".");
            if (idx > 0) {
                String suffix = path.substring(idx);
                String mime = staticFiles.get(suffix);

                if (mime != null) {
                    context.headerSet(CACHE_CONTROL, "max-age=6000");
                    context.headerSet(LAST_MODIFIED, modified_time.toString());
                    context.contentType(mime);
                }
            }

            context.status(200);
            context.output(uri.openStream());
        }
    }

    private static final Date modified_time = new Date();
}
