package features;

import org.junit.Test;
import org.noear.solon.extend.staticfiles.StaticMimes;
import org.noear.solon.extend.staticfiles.StaticResourceHandler;

import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Map;

/**
 * @author noear 2022/2/7 created
 */
public class MimeTest {
    @Test
    public void test(){
        FileNameMap tmp = URLConnection.getFileNameMap();

        if(tmp == null){
            return;
        }

        System.out.println(tmp.getContentTypeFor(".jpg"));
        System.out.println(tmp.getContentTypeFor("/bbb/c/xxx.jpg"));
        System.out.println(tmp.getContentTypeFor("/bbb/c/xxx.jpg/"));
        System.out.println(tmp.getContentTypeFor("/cxxx.xxx"));

    }

    @Test
    public void test1(){
        FileNameMap tmp = URLConnection.getFileNameMap();

        for(Map.Entry<String,String> kv: StaticMimes.getMap().entrySet()){
            System.out.println(tmp.getContentTypeFor(kv.getKey()));
            assert  tmp.getContentTypeFor(kv.getKey()) != null;
        }
    }

    @Test
    public void test2(){
        FileNameMap tmp = URLConnection.getFileNameMap();
        StaticResourceHandler handler = new StaticResourceHandler();
        String path = "/xxx/xxx.jpg";

        long start = System.currentTimeMillis();
        for(int i=0; i<100000; i++) {
            //String ext = findByExtName(path);
            //StaticMimes.get(ext);
            tmp.getContentTypeFor(".jpg");
//            handler.test(path);
        }

        System.out.println(System.currentTimeMillis() - start);
    }

    public String findByExtName(String path) {
        String ext = "";
        int pos = path.lastIndexOf(35);
        if (pos > 0) {
            path = path.substring(0, pos - 1);
        }

        pos = path.lastIndexOf(46);
        pos = Math.max(pos, path.lastIndexOf(47));
        pos = Math.max(pos, path.lastIndexOf(63));
        if (pos != -1 && path.charAt(pos) == '.') {
            ext = path.substring(pos).toLowerCase();
        }

        return ext;
    }
}
