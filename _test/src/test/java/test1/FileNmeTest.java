package test1;

import org.junit.Test;

import java.net.URLEncoder;

/**
 * @author noear 2021/8/4 created
 */
public class FileNmeTest {
    @Test
    public void test() throws Exception{
        String fileName = URLEncoder.encode("没有耳多 aaa.jar","utf-8");

        System.out.println(fileName);
        System.out.println(URLEncoder.encode(fileName,"utf-8"));
    }

    @Test
    public void test1(){
        System.out.println(System.getenv("file.encoding"));
        System.out.println(System.getProperty("file.encoding"));
    }
}
