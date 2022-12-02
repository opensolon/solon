package org.noear.solon.web.webdav;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;

import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * webdav抽象拦截器
 * @author 阿范
 */
public abstract class AbstractHandler implements Handler {
    /**
     * 获取登录用户名
     * 未登录/登录失败返回空
     * @param ctx
     * @return
     */
    public abstract String getLoginUser(Context ctx);

    /**
     * 文件管理系统
     * @return
     */
    public abstract FileSystem fileSystem();

    /**
     * 访问前缀
     * @return
     */
    public abstract String prefix();
    private boolean range;
    public AbstractHandler(){
        range = Solon.cfg().getBool("solon.webdav.range",true);
    }
    @Override
    public void handle(Context ctx){
        ctx.headerSet("Pragma","no-cache");
        ctx.headerSet("Cache-Control","no-cache");
        ctx.headerSet("X-DAV-BY","solon");
        if(Utils.isBlank(this.getLoginUser(ctx))){
            ctx.headerSet("WWW-Authenticate","Basic realm=\"solon\"");
            ctx.status(HttpStatus.UNAUTHORIZED.value());
            return;
        }
        int status = HttpStatus.BAD_REQUEST.value();
        if (this.fileSystem() == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        } else {
            try{
                switch(ctx.method()){
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
                        status = this.handleMkcol(ctx);
                        break;
                    case "COPY":
                    case "MOVE":
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
            }catch (WebDavActionException e){
                status = e.getCode();
            }catch (Exception e){
                e.printStackTrace();
                status = HttpStatus.BAD_REQUEST.value();
            }
        }
        if(status == 0){
            status = HttpStatus.OK.value();
        }
        ctx.status(status);
    }

    private int handleProppatch(Context ctx) {
        //PROPPATCH
        String reqPath = this.stripPrefix(ctx.path());
        String href = this.prefix()+"/"+reqPath;
        ctx.output("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<D:multistatus xmlns:D=\"DAV:\">\n" +
                        "\n" +
                        "\t\t<D:response>\n" +
                        "\t\t\t<D:href>"+href+"</D:href>\n" +
                        "\t\t\t<D:propstat>\n" +
                        "\t\t\t\t<D:prop>\n" +
                        "\t\t\t\t\t<m:Win32LastAccessTime xmlns:m=\"urn:schemas-microsoft-com:\" />\n" +
                        "\t\t\t\t\t<m:Win32CreationTime xmlns:m=\"urn:schemas-microsoft-com:\" />\n" +
                        "\t\t\t\t\t<m:Win32LastModifiedTime xmlns:m=\"urn:schemas-microsoft-com:\" />\n" +
                        "\t\t\t\t\t<m:Win32FileAttributes xmlns:m=\"urn:schemas-microsoft-com:\" />\n" +
                        "\t\t\t\t</D:prop>\n" +
                        "\t\t\t\t<D:status>HTTP/1.1 HttpStatus.OK.value() OK</D:status>\n" +
                        "\t\t\t</D:propstat>\n" +
                        "\t\t</D:response>\n" +
                        "</D:multistatus>\n");
        return HttpStatus.MULTI_STATUS.value();
    }
    private int parseDepth(String s) {
        switch(s){
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
        //reqPath结构为  /你好/2/  会变成 你好/2
        String reqPath = this.stripPrefix(ctx.path());
        FileInfo fi = this.fileSystem().fileInfo(reqPath);
        if(fi == null){
            return HttpStatus.NOT_FOUND.value();
        }
        int depth = -1;
        String hdr = ctx.header("Depth");
        if(Utils.isNotBlank(hdr)){
            depth = parseDepth(hdr);
            if(depth == -2){
                return HttpStatus.BAD_REQUEST.value();
            }
        }
        if(depth == -1){
            depth = 1;
        }
        String itemResponse = this.toItemResponse(reqPath,fi);
        if(!fi.isDir() || depth == 0){
            ctx.output(this.toItemListResponse(itemResponse));
            return HttpStatus.MULTI_STATUS.value();
        }
        List<FileInfo> childs = this.fileSystem().fileList(reqPath);
        List<String> list = new ArrayList<>();
        list.add(itemResponse);
        if(childs != null && childs.size() > 0){
            for(FileInfo info:childs){
                list.add(this.toItemResponse(reqPath+"/"+info.name(),info));
            }
        }
        String[] arr = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }
        String out = this.toItemListResponse(arr);
        ctx.output(out);
        return HttpStatus.MULTI_STATUS.value();
    }

    private String toItemListResponse(String... itemResponse) {
        String template = "<D:multistatus xmlns:D=\"DAV:\"> \n" +
                "\t{{itemResponse}}\n" +
                "</D:multistatus>";
        StringBuilder sb = new StringBuilder();
        for (String tmp:itemResponse){
            sb.append(tmp);
        }
        return template.replace("{{itemResponse}}",sb.toString());
    }

    private String toItemResponse(String reqPath,FileInfo fi) {
        String template = "<D:response>\n" +
                "\t\t\t<D:href>{{href}}</D:href>\n" +
                "\t\t\t<D:propstat>\n" +
                "\t\t\t\t<D:prop>\n" +
                "\t\t\t\t\t<D:getlastmodified>{{update}}</D:getlastmodified>\n" +
                "\t\t\t\t\t<D:creationdate>{{create}}</D:creationdate>\n" +
                "\t\t\t\t\t<D:getcontentlength>{{size}}</D:getcontentlength>\n" +
                "\t\t\t\t\t<D:resourcetype>{{dir}}</D:resourcetype><D:getcontenttype>{{mmi}}</D:getcontenttype>\n" +
                "\t\t\t\t</D:prop>\n" +
                "\t\t\t\t<D:status>HTTP/1.1 HttpStatus.OK.value() OK</D:status>\n" +
                "\t\t\t</D:propstat>\n" +
                "\t\t</D:response>";
        template = template.replace("{{href}}",this.prefix()+"/"+reqPath);
        template = template.replace("{{update}}", WebdavUtil.parse(fi.update()).toString());
        template = template.replace("{{create}}",WebdavUtil.formatTz(WebdavUtil.parse(fi.create())));
        template = template.replace("{{size}}",fi.size()+"");
        template = template.replace("{{dir}}",fi.isDir()?"<D:collection/>":"");
        template = template.replace("{{mmi}}",this.fileSystem().fileMime(fi));
        return template;
    }

    private int handleUnlock(Context ctx) {
        return HttpStatus.NO_CONTENT.value();
    }

    private int handleLock(Context ctx) {
        String token = UUID.randomUUID().toString();
        ctx.headerSet("Lock-Token",token);
        ctx.contentType("application/xml; charset=utf-8");
        ctx.output("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"+
                "<D:prop xmlns:D=\"DAV:\"><D:lockdiscovery><D:activelock>\n"+
                "	<D:locktype><D:write/></D:locktype>\n"+
                "	<D:lockscope><D:exclusive/></D:lockscope>\n"+
                "	<D:depth>infinity</D:depth>\n"+
                "	<D:owner>"+getLoginUser(ctx)+"</D:owner>\n"+
                "	<D:locktoken><D:href>"+token+"</D:href></D:locktoken>\n"+
                "</D:activelock></D:lockdiscovery></D:prop>");
        return HttpStatus.CREATED.value();
    }

    private int handleCopyMove(Context ctx) {
        String reqPath = stripPrefix(ctx.path());
        String descPath = stripPrefix(ctx.header("Destination"));
        if(reqPath.equals(descPath)){
            return HttpStatus.FORBIDDEN.value();
        }
        boolean flag;
        if(ctx.method().equals("COPY")){
            flag = this.fileSystem().copy(reqPath,descPath);
        }else{
            flag = this.fileSystem().move(reqPath,descPath);
        }
        return flag?HttpStatus.CREATED.value():HttpStatus.NOT_FOUND.value();
    }

    private int handleMkcol(Context ctx) {
        String reqPath = stripPrefix(ctx.path());
        boolean flag = this.fileSystem().mkdir(reqPath);
        return flag?HttpStatus.CREATED.value():HttpStatus.METHOD_NOT_ALLOWED.value();
    }

    private int handlePut(Context ctx) throws Exception {
        String reqPath = stripPrefix(ctx.path());
        boolean flag = this.fileSystem().putFile(reqPath,ctx.bodyAsStream());
        return flag?HttpStatus.CREATED.value():HttpStatus.METHOD_NOT_ALLOWED.value();
    }
    private FileInfo infoCheck(){
        return null;
    }
    private int handleDelete(Context ctx) {
        String reqPath = stripPrefix(ctx.path());
        boolean flag = this.fileSystem().del(reqPath);
        if(!flag){
            return HttpStatus.FORBIDDEN.value();
        }
        return 0;
    }
    private String urlEncode(String url){
        try{
            return URLEncoder.encode(url,"utf-8");
        }catch (Exception e){
            return url;
        }
    }

    private String urlDecode(String url){
        try{
            return URLDecoder.decode(url,"utf-8");
        }catch (Exception e){
            return url;
        }
    }

    private int handleGetHeadPost(Context ctx) {
        String reqPath = stripPrefix(ctx.path());
        FileInfo fi = this.fileSystem().fileInfo(reqPath);
        if(fi == null){
            return HttpStatus.NOT_FOUND.value();
        }
        if(fi.isDir()){
            return HttpStatus.METHOD_NOT_ALLOWED.value();
        }
        String etag = this.fileSystem().findEtag(reqPath,fi);
        if(Utils.isBlank(etag)){
            return HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
        ctx.headerSet("ETag",etag);
        ctx.headerSet("Last-Modified",WebdavUtil.parse(fi.update()).toString());
        int type = 3;
        if ("HEAD".equals(ctx.method())) {
            type = 1;
        }
        long start = 0;
        long end = 0;
        if (type == 3) {
            String range = ctx.header("Range");
            if (Utils.isNotBlank(range)) {
                type = 1;
                String[] sz = range.split("=")[1].split(",")[0].split("-");
                if (sz.length > 0) {
                    start = Long.parseLong(sz[0].trim());
                    if (sz.length > 1) {
                        end = Long.parseLong(sz[1].trim());
                    } else {
                        end = fi.size() - 1;
                    }
                    type = 2;
                }
            }
        }
        ctx.headerSet("Content-Type", "application/octet-stream");
        ctx.headerSet("Accept-Ranges", "bytes");
        ctx.headerSet("Content-Disposition", "attachment; filename=\"" + urlEncode(fi.name()) + "\"");
        if (type == 1 || type == 3) {
            start = 0;
            end = fi.size() - 1;
        }
        ctx.headerSet("Content-Length", String.valueOf(end - start + 1));
        if (type == 1) {
            return HttpStatus.OK.value();
        }
        long length = end - start + 1;
        InputStream resIn;
        if (this.range) {
            ctx.headerSet("Content-Range", "bytes " + start + "-" + end + "/" + fi.size());
            resIn = this.fileSystem().fileInputStream(reqPath,start, length);
        } else {
            resIn = this.fileSystem().fileInputStream(reqPath,0, 0);
        }
        try{
            ctx.output(resIn);
        }catch (Exception e){

        }
        if (type == 2) {
            return HttpStatus.PARTIAL_CONTENT.value();
        }
        return 0;
    }

    private int handleOptions(Context ctx) {
        String reqPath = stripPrefix(ctx.path());
        String allow = "OPTIONS, LOCK, PUT, MKCOL";
        FileInfo fi = this.fileSystem().fileInfo(reqPath);
        if(fi != null){
            if(fi.isDir()){
                allow = "OPTIONS, LOCK, DELETE, PROPPATCH, COPY, MOVE, UNLOCK, PROPFIND";
            }else{
                allow = "OPTIONS, LOCK, GET, HEAD, POST, DELETE, PROPPATCH, COPY, MOVE, UNLOCK, PROPFIND, PUT";
            }
        }
        ctx.headerSet("Allow", allow);
        ctx.headerSet("DAV", "1, 2");
        ctx.headerSet("MS-Author-Via", "DAV");
        return 0;
    }

    private String stripPrefix(String p) {
        p = urlDecode(p);
        int index = p.indexOf(this.prefix());
        if(index == -1){
            throw new WebDavActionException(HttpStatus.NOT_FOUND.value());
        }
        String r = p.substring(index+this.prefix().length());
        if(r.length() < p.length()){
            if(r.endsWith("/")){
                r = r.substring(0,r.length()-1);
            }
            if(r.startsWith("/")){
                r = r.substring(1);
            }
            return r;
        }
        throw new WebDavActionException(HttpStatus.NOT_FOUND.value());
    }
}
