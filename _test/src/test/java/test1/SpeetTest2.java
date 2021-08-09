package test1;

import org.junit.Test;
import org.noear.solon.extend.staticfiles.StaticMimes;

import java.util.Map;
import java.util.regex.Pattern;

public class SpeetTest2 {
    long time_start;
    long time_end;

    @Test
    public void test1() {
        String expr = "(" + String.join("|", StaticMimes.instance().keySet()) + ")$";

        String path1 = "/file.txt";
        String path2 = "/file.eot";

        Pattern _rule = Pattern.compile(expr, Pattern.CASE_INSENSITIVE);

        time_start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            _rule.matcher(path1);
        }
        time_end = System.currentTimeMillis();
        System.out.println("path1: " + (time_end - time_start));

        time_start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            _rule.matcher(path2);
        }
        time_end = System.currentTimeMillis();
        System.out.println("path2: " + (time_end - time_start));
    }

    @Test
    public void test2() {
        String expr = "(" + String.join("|", StaticMimes.instance().keySet()) + ")$";

        String path1 = "/file.txt";
        String path2 = "/file.eot";

        Pattern _rule = Pattern.compile(expr, Pattern.CASE_INSENSITIVE);

        time_start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            for(Map.Entry<String,String> kv : StaticMimes.instance().entrySet()){
                if(path1.indexOf(kv.getKey()) > 0){
                    break;
                }
            }
        }
        time_end = System.currentTimeMillis();
        System.out.println("path1: " + (time_end - time_start));

        time_start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            for(Map.Entry<String,String> kv : StaticMimes.instance().entrySet()){
                if(path2.indexOf(kv.getKey()) > 0){
                    break;
                }
            }
        }
        time_end = System.currentTimeMillis();
        System.out.println("path2: " + (time_end - time_start));
    }
}
