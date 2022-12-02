package org.noear.solon.web.webdav;

import java.io.InputStream;
import java.util.List;

/**
 * webdav文件系统
 *  @author 阿范
 */
public interface FileSystem {
    FileInfo fileInfo(String reqPath);
    String fileMime(FileInfo fi);
    List<FileInfo> fileList(String reqPath);
    String findEtag(String reqPath, FileInfo fi);
    InputStream fileInputStream(String reqPath, long start,long length);
    boolean putFile(String reqPath, InputStream in);

    boolean del(String reqPath);

    boolean copy(String reqPath, String descPath);

    boolean move(String reqPath, String descPath);

    boolean mkdir(String reqPath);
}
