package org.noear.solonboot;

import java.io.*;
import java.net.URL;
import java.util.*;

/*内部用工具*/
public final class XUtil {
    /*生成UGID*/
    public static String guid(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    /*检查字符串是否为空*/
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /*根据字符串生成一个类*/
    public static <T> T newClass(String classFullname) {
        try {
            Class cls = Class.forName(classFullname);
            return (T) cls.newInstance();
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    /*获取资源URL*/
    public static URL getResource(String name) {
        URL url = XUtil.class.getResource(name);
        if (url == null) {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (loader != null) {
                url = loader.getResource(name);
            } else {
                url = ClassLoader.getSystemResource(name);
            }
        }

        return url;
    }

    public static String addPath(String path1, String path2) {
        int idx = path1.lastIndexOf("/") + 1;
        return path1.substring(0, idx) + path2;
    }

    /*获取异常的完整内容*/
    public static String getFullStackTrace(Exception ex){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        ex.printStackTrace(pw);

        return sw.getBuffer().toString();
    }
}
