package features;

import org.junit.Test;
import org.noear.solon.core.util.PathUtil;

public class PathTest {
    @Test
    public void test(){
        assert  PathUtil.mergePath("/user/*","").equals("/user/");

        assert  PathUtil.mergePath("","/user/*").equals("/user/*");

        assert  PathUtil.mergePath("/","/user/*").equals("/user/*");
    }
}
