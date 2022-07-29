package features;

import org.junit.Test;

import java.io.File;

/**
 * @author noear 2022/7/11 created
 */
public class PathTest {
    @Test
    public void test(){
        File file =  new File("upload","xxx.jpg");

        System.out.println(file.toURI());
    }
}
