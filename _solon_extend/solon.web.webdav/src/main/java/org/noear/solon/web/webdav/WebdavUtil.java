package org.noear.solon.web.webdav;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WebdavUtil {
    private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static List<SimpleDateFormat> sdfs = new ArrayList<>();
    static{
        sdfs.add(sdf1);
        sdfs.add(sdf2);
        sdfs.add(sdf3);
    }

    public static Date parse(String text){
        for(SimpleDateFormat sdf:sdfs){
            try{
                return sdf.parse(text);
            }catch (Exception e){
            }
        }
        throw new RuntimeException(text+"can not cast to date");
    }

    public static String formatTz(Date date){
        try{
            return sdf2.format(date);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static String formatDateTime(Date date){
        try{
            return sdf1.format(date);
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    public static InputStream getInputStream(String path) {
        try{
            return new FileInputStream(path);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static String md5(String s) {
        StringBuffer sb = new StringBuffer(32);
        try {
            MessageDigest md 	= MessageDigest.getInstance("MD5");
            byte[] array 		= md.digest(s.getBytes("utf-8"));

            for (int i = 0; i < array.length; i++) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).toUpperCase().substring(1, 3));
            }
        } catch (Exception e) {
            return null;
        }
        return sb.toString();

    }

    public static void writeFromStream(InputStream in, String path) {
        try{
            FileOutputStream fos = new FileOutputStream(path);
            try{
                byte[] b = new byte[8192];
                int c;
                while ((c = in.read(b))!=-1){
                    fos.write(b,0,c);
                }
            }catch (Exception e){
            }
            fos.close();
        }catch (Exception e){
        }
    }

    public static boolean exist(String path) {
        return new File(path).exists();
    }

    public static File mkdir(String reqPath) {
        File file = new File(reqPath);
        if(file.exists()){
            return file;
        }
        file.mkdir();
        return file;
    }
}
