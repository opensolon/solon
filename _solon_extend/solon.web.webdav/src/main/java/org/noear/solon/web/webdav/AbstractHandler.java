package org.noear.solon.web.webdav;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.*;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * webdav抽象拦截器
 *
 * @author 阿范
 */
public abstract class AbstractHandler implements Handler {
    private boolean range;

    public AbstractHandler(boolean range) {
        this.range = range;
    }

    /**
     * 获取用户名
     * 获取不到返回空
     * 表示未登录
     *
     * @param ctx
     * @return
     */
    public abstract String user(Context ctx);

    /**
     * 文件管理系统
     *
     * @return
     */
    public abstract FileSystem fileSystem();

    /**
     * 访问前缀
     *
     * @return
     */
    public abstract String prefix();

    @Override
    public void handle(Context ctx) {
        try {
            ctx.contentType("text/xml; charset=UTF-8");
            ctx.headerSet("Pragma", "no-cache");
            ctx.headerSet("Cache-Control", "no-cache");
            ctx.headerSet("X-DAV-BY", "webos");
            ctx.headerSet("Access-Control-Allow-Origin", "*");
            ctx.headerSet("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE, HEAD, MOVE, COPY, PUT, MKCOL, PROPFIND, PROPPATCH, LOCK, UNLOCK");
            ctx.headerSet("Access-Control-Allow-Headers", "ETag, Content-Type, Content-Length, Accept-Encoding, X-Requested-with, Origin, Authorization");
            ctx.headerSet("Access-Control-Allow-Credentials", "true");
            ctx.headerSet("Access-Control-Max-Age", "3600");
            if (StrUtil.isBlank(this.user(ctx))) {
                ctx.headerSet("WWW-Authenticate", "Basic realm=\"webos\"");
                ctx.status(401);
                return;
            }
            int status = 400;
            if (this.fileSystem() == null) {
                status = 500;
            } else {
                try {
                    switch (ctx.method()) {
                        case "OPTIONS":
                            status = this.handleOptions(ctx);
                            break;
                        case "GET":
                        case "HEAD":
                        case "POST":
                            status = this.handleGetHeadPost(ctx);
                            break;
                        case "DELETE":
                            status = this.handleDelete(ctx);
                            break;
                        case "PUT":
                            status = this.handlePut(ctx);
                            break;
                        case "MKCOL":
                        case "KCOL":
                            status = this.handleMkcol(ctx);
                            break;
                        case "COPY":
                        case "MOVE":
                        case "OVE":
                            status = this.handleCopyMove(ctx);
                            break;
                        case "LOCK":
                            status = this.handleLock(ctx);
                            break;
                        case "UNLOCK":
                            status = this.handleUnlock(ctx);
                            break;
                        case "PROPFIND":
                            status = this.handlePropfind(ctx);
                            break;
                        case "PROPPATCH":
                            status = this.handleProppatch(ctx);
                            break;
                    }
                } catch (WebDavActionException e) {
                    //e.printStackTrace();
                    status = e.getCode();
                } catch (Exception e) {
                    //e.printStackTrace();
                    status = 400;
                }
            }
            if (status == 0) {
                status = 200;
            }
            ctx.status(status);
        } catch (Exception e) {

        }
    }

    private int handleProppatch(Context ctx) {
        String reqPath = this.stripPrefix(ctx.path());
        String href = this.prefix() + "/" + reqPath;
        href = URLUtil.encode(href);
        String template = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<D:multistatus xmlns:D=\"DAV:\">\n" +
                "\n" +
                "\t\t<D:response>\n" +
                "<D:href>{}</D:href>\n" +
                "<D:propstat>\n" +
                "\t<D:prop>\n" +
                "\t\t<m:Win32LastAccessTime xmlns:m=\"urn:schemas-microsoft-com:\" />\n" +
                "\t\t<m:Win32CreationTime xmlns:m=\"urn:schemas-microsoft-com:\" />\n" +
                "\t\t<m:Win32LastModifiedTime xmlns:m=\"urn:schemas-microsoft-com:\" />\n" +
                "\t\t<m:Win32FileAttributes xmlns:m=\"urn:schemas-microsoft-com:\" />\n" +
                "\t</D:prop>\n" +
                "\t<D:status>HTTP/1.1 200 OK</D:status>\n" +
                "</D:propstat>\n" +
                "\t\t</D:response>\n" +
                "</D:multistatus>\n";
        ctx.output(StrUtil.format(template, href));
        return 207;
    }

    private int parseDepth(String s) {
        switch (s) {
            case "0":
                return 0;
            case "1":
                return 1;
            case "infinity":
                return -1;
        }
        return -2;
    }

    private int handlePropfind(Context ctx) throws Exception {
        String reqPath = this.stripPrefix(ctx.path());
        FileInfo fi = this.fileSystem().fileInfo(reqPath);
        if (fi == null) {
            return 404;
        }
        int depth = -1;
        String hdr = ctx.header("Depth");
        if (StrUtil.isNotBlank(hdr)) {
            depth = parseDepth(hdr);
            if (depth == -2) {
                return 400;
            }
        }
        if (depth == -1) {
            depth = 1;
        }
        String itemResponse = this.toItemResponse(reqPath, fi);
        if (!fi.isDir() || depth == 0) {
            ctx.output(this.toItemListResponse(itemResponse));
            return 207;
        }
        List<FileInfo> childs = this.fileSystem().fileList(reqPath);
        List<String> list = CollUtil.newArrayList(itemResponse);
        if (CollUtil.isNotEmpty(childs)) {
            for (FileInfo info : childs) {
                String tmp = StrUtil.isBlank(reqPath) ? info.name() : reqPath + "/" + info.name();
                list.add(this.toItemResponse(tmp, info));
            }
        }
        String out = this.toItemListResponse(ArrayUtil.toArray(list, String.class));
        ctx.output(out);
        return 207;
    }

    private String toItemListResponse(String... itemResponse) {
        String template = "<D:multistatus xmlns:D=\"DAV:\"> \n" +
                "\t{}\n" +
                "</D:multistatus>";
        StringBuilder sb = new StringBuilder();
        for (String tmp : itemResponse) {
            sb.append(tmp);
        }
        return StrUtil.format(template, sb.toString());
    }

    private String toItemResponse(String reqPath, FileInfo fi) {
        String template = "<D:response>\n" +
                "<D:href>{}</D:href>\n" +
                "<D:propstat>\n" +
                "\t<D:prop>\n" +
                "\t\t<D:getlastmodified>{}</D:getlastmodified>\n" +
                "\t\t<D:creationdate>{}</D:creationdate>\n" +
                "\t\t<D:getcontentlength>{}</D:getcontentlength>\n" +
                "\t\t<D:resourcetype>{}</D:resourcetype><D:getcontenttype>{}</D:getcontenttype>\n" +
                "\t</D:prop>\n" +
                "\t<D:status>HTTP/1.1 200 OK</D:status>\n" +
                "</D:propstat>\n" +
                "\t</D:response>";
        String href = StrUtil.isBlank(reqPath) ? this.prefix() : this.prefix() + "/" + reqPath;
        href = URLUtil.encode(href);
        return StrUtil.format(template,
                href,
                DateUtil.parse(fi.update()).toJdkDate().toString(),
                DateUtil.parse(fi.create()).toString("yyyy-MM-dd'T'HH:mm:ss'Z'"),
                fi.size(),
                fi.isDir() ? "<D:collection/>" : "",
                this.fileSystem().fileMime(fi)
        );
    }

    private int handleUnlock(Context ctx) {
        String token = StrUtil.subBetween(ctx.header("Lock-Token"), "<", ">");
        return 204;
    }

    private int handleLock(Context ctx) {
        try {
            Map map = XmlUtil.xmlToMap(ctx.body());
            String lockscope = StrUtil.subBetween(XmlUtil.mapToXmlStr((Map) map.get("D:lockscope")), "<xml>", "</xml>");
            String locktype = StrUtil.subBetween(XmlUtil.mapToXmlStr((Map) map.get("D:locktype")), "<xml>", "</xml>");
            String owner = StrUtil.subBetween(XmlUtil.mapToXmlStr((Map) map.get("D:owner")), "<xml>", "</xml>");
            String token = IdUtil.fastSimpleUUID();
            ctx.headerSet("Lock-Token", "<" + token + ">");
            ctx.output(StrUtil.format("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                            "<D:prop xmlns:D=\"DAV:\"><D:lockdiscovery><D:activelock>\n" +
                            "\t<D:locktype>{}</D:locktype>\n" +
                            "\t<D:lockscope>{}</D:lockscope>\n" +
                            "\t<D:depth>infinity</D:depth>\n" +
                            "\t<D:owner>{}</D:owner>\n" +
                            "\t<D:timeout>Infinite</D:timeout>\n" +
                            "\t<D:locktoken><D:href>{}</D:href></D:locktoken>\n" +
                            "</D:activelock></D:lockdiscovery></D:prop>",
                    locktype, lockscope, owner, token));
            return 200;
        } catch (Exception e) {
            return 400;
        }
    }

    private int handleCopyMove(Context ctx) {
        String reqPath = stripPrefix(ctx.path());
        String descPath = stripPrefix(ctx.header("Destination"));
        if (StrUtil.equals(reqPath, descPath)) {
            return 403;
        }
        boolean flag = false;
        if (ctx.method().equals("COPY") || ctx.method().equals("OPY")) {
            flag = this.fileSystem().copy(reqPath, descPath);
        } else if (ctx.method().equals("MOVE") || ctx.method().equals("OVE")) {
            flag = this.fileSystem().move(reqPath, descPath);
        }
        return flag ? 201 : 404;
    }

    private int handleMkcol(Context ctx) {
        String reqPath = stripPrefix(ctx.path());
        boolean flag = this.fileSystem().mkdir(reqPath);
        return flag ? 201 : 405;
    }

    private int handlePut(Context ctx) throws Exception {
        if (StrUtil.isBlank(ctx.header("If"))) {
            return 200;
        }
        String reqPath = stripPrefix(ctx.path());
        boolean flag = this.fileSystem().putFile(reqPath, ctx.bodyAsStream());
        return flag ? 204 : 405;
    }

    private int handleDelete(Context ctx) {
        String reqPath = stripPrefix(ctx.path());
        boolean flag = this.fileSystem().del(reqPath);
        if (!flag) {
            return 403;
        }
        return 0;
    }

    private int handleGetHeadPost(Context ctx) {
        String reqPath = stripPrefix(ctx.path());
        String url = this.fileSystem().fileUrl(reqPath);
        if (StrUtil.isNotBlank(url)) {
            ctx.headerSet("Location", url);
            ctx.headerSet("Access-Control-Allow-Origin", "*");
            ctx.headerSet("Access-Control-Allow-Credentials", "true");
            ctx.headerSet("Access-Control-Allow-Methods", "*");
            ctx.headerSet("Access-Control-Allow-Headers", "*");
            ctx.headerSet("Access-Control-Expose-Headers", "*");
            ctx.headerSet("Access-Control-Max-Age", "86400");
            return 302;
        }
        FileInfo fi = this.fileSystem().fileInfo(reqPath);
        if (fi == null) {
            return 404;
        }
        if (fi.isDir()) {
            return 405;
        }
        String etag = this.fileSystem().findEtag(reqPath, fi);
        if (StrUtil.isBlank(etag)) {
            return 500;
        }
        ctx.headerSet("ETag", etag);
        ctx.headerSet("Last-Modified", DateUtil.parse(fi.update()).toJdkDate().toString());
        int type = 3;
        if (StrUtil.equals(ctx.method(), "HEAD")) {
            type = 1;
        }
        long start = 0;
        long end = 0;
        if (type == 3) {
            String range = ctx.header("Range");
            if (StrUtil.isNotBlank(range)) {
                type = 1;
                String[] sz = range.split("=")[1].split(",")[0].split("-");
                if (sz.length > 0) {
                    start = Convert.toLong(sz[0].trim());
                    if (sz.length > 1) {
                        end = Convert.toLong(sz[1].trim());
                    } else {
                        end = fi.size() - 1;
                    }
                    type = 2;
                }
            }
        }
        ctx.headerSet("Content-Type", "application/octet-stream");
        ctx.headerSet("Accept-Ranges", "bytes");
        ctx.headerSet("Content-Disposition", "attachment; filename=\"" + URLUtil.encodeAll(fi.name()) + "\"");
        if (type == 1 || type == 3) {
            start = 0;
            end = fi.size() - 1;
        }
        ctx.headerSet("Content-Length", Convert.toStr(end - start + 1));
        if (type == 1) {
            return 200;
        }
        long length = end - start + 1;
        InputStream resIn;
        if (this.range) {
            ctx.headerSet("Content-Range", "bytes " + start + "-" + end + "/" + fi.size());
            resIn = this.fileSystem().fileInputStream(reqPath, start, length);
        } else {
            resIn = this.fileSystem().fileInputStream(reqPath, 0, 0);
        }
        try {
            ctx.output(resIn);
        } catch (Exception e) {

        }
        if (type == 2) {
            return 206;
        }
        return 0;
    }

    private int handleOptions(Context ctx) {
        String reqPath = stripPrefix(ctx.path());
        String allow = "OPTIONS, LOCK, PUT, MKCOL";
        FileInfo fi = this.fileSystem().fileInfo(reqPath);
        if (fi != null) {
            if (fi.isDir()) {
                allow = "OPTIONS, LOCK, DELETE, PROPPATCH, COPY, MOVE, UNLOCK, PROPFIND";
            } else {
                allow = "OPTIONS, LOCK, GET, HEAD, POST, DELETE, PROPPATCH, COPY, MOVE, UNLOCK, PROPFIND, PUT";
            }
        }
        ctx.headerSet("Allow", allow);
        ctx.headerSet("DAV", "1, 2");
        ctx.headerSet("MS-Author-Via", "DAV");
        return 0;
    }

    private String stripPrefix(String p) {
        p = URLUtil.decode(p);
        int index = p.indexOf(this.prefix());
        if (index == -1) {
            throw new WebDavActionException(404);
        }
        String r = p.substring(index + this.prefix().length());
        if (r.length() < p.length()) {
            if (r.endsWith("/")) {
                r = r.substring(0, r.length() - 1);
            }
            if (r.startsWith("/")) {
                r = r.substring(1);
            }
            return r;
        }
        throw new WebDavActionException(404);
    }
}