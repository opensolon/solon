package features;

import org.junit.Test;
import org.noear.solon.core.util.PathUtil;

public class PathTest {
    @Test
    public void test(){
        String tmp = PathUtil.mergePath("/user/*","");

        System.out.println(tmp);

        assert "/user/".equals(tmp);
    }
}
