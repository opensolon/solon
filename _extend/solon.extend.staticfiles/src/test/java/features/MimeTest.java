package features;

import org.junit.Test;

import java.net.FileNameMap;
import java.net.URLConnection;

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
    }
}
