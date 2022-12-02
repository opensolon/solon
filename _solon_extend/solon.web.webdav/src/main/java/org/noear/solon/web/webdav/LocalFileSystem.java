package org.noear.solon.web.webdav;

import cn.hutool.core.io.FileUtil;
import org.noear.solon.Utils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * webdav本地文件系统实现
 *  @author 阿范
 */
public class LocalFileSystem implements FileSystem {
    private static Map<String,Long> lockMap = new ConcurrentHashMap<>();
    private String rootPath;
    public LocalFileSystem(String rootPath){
        this.rootPath = rootPath;
    }
    private String realPath(String path){
        if(Utils.isBlank(path)){
            return rootPath;
        }
        return rootPath+"/"+path;
    }

    private FileInfo file2Info(File file){
        if(file != null && !file.exists()){
            return null;
        }
        return new FileInfo() {
            @Override
            public String name() {
                return file==null?"":file.getName();
            }

            @Override
            public boolean isDir() {
                return file==null?true:file.isDirectory();
            }

            @Override
            public long size() {
                return file==null?0:file.length();
            }

            @Override
            public String update() {
                Date date = file==null?new Date(): new Date(file.lastModified());
                return WebdavUtil.formatDateTime(date);
            }

            @Override
            public String create() {
                Date date = file==null?new Date(): new Date(file.lastModified());
                return WebdavUtil.formatDateTime(date);
            }
        };
    }
    @Override
    public FileInfo fileInfo(String path) {
        if(Utils.isBlank(path)){
            return file2Info(null);
        }else{
            path = realPath(path);
            File file = new File(path);
            return file2Info(file);
        }
    }

    @Override
    public String fileMime(FileInfo fi) {
        return fi.isDir()?"httpd/unix-directory":"text/plain";
    }

    @Override
    public List<FileInfo> fileList(String path) {
        path = realPath(path);
        File file = new File(path);
        if(!file.exists()){
            return null;
        }
        File[] files = file.listFiles();
        List<FileInfo> list = new ArrayList<>();
        for (File tmp:files){
            FileInfo fi = file2Info(tmp);
            if(fi != null){
                list.add(fi);
            }
        }
        return list;
    }

    @Override
    public String findEtag(String path, FileInfo fi) {
        path = realPath(path);
        return "W/\""+ WebdavUtil.md5(fi.update()+path) +"\"";
    }

    @Override
    public InputStream fileInputStream(String path,long start,long length) {
        path = realPath(path);
        InputStream in = WebdavUtil.getInputStream(path);
        if(length == 0){
            return in;
        }else{
            return new ShardingInputStream(in,start,length);
        }
    }

    @Override
    public boolean putFile(String path, InputStream in) {
        path = realPath(path);
        WebdavUtil.writeFromStream(in,path);
        return true;
    }

    @Override
    public boolean del(String path) {
        path = realPath(path);
        if(!WebdavUtil.exist(path)){
            return true;
        }
        return new File(path).delete();
    }

    @Override
    public boolean copy(String reqPath, String descPath) {
        try{
            reqPath = realPath(reqPath);
            descPath = realPath(descPath);
            File source = new File(reqPath);
            File target = new File(descPath);
            FileUtil.copy(source,new File(target.getParent()),true);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public boolean move(String reqPath, String descPath) {
       try{
           reqPath = realPath(reqPath);
           descPath = realPath(descPath);
           File source = new File(reqPath);
           File target = new File(descPath);
           if(source.getParent().equals(target.getParent())){
               return source.renameTo(new File(descPath));
           }
           FileUtil.move(source,new File(target.getParent()),true);
           return true;
       }catch (Exception e){
           e.printStackTrace();
           return false;
       }

    }

    @Override
    public boolean mkdir(String reqPath) {
        reqPath = realPath(reqPath);
        File file = WebdavUtil.mkdir(reqPath);
        return file.exists();
    }
}
