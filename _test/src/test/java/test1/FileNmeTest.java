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
    }
}
