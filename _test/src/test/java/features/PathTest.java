package features;

import org.junit.Test;
import org.noear.solon.core.util.PathUtil;
import org.noear.solon.extend.staticfiles.StaticMappings;

public class PathTest {
    @Test
    public void test(){
        assert  PathUtil.mergePath("/user/*","").equals("/user/");

        assert  PathUtil.mergePath("","/user/*").equals("/user/*");

        assert  PathUtil.mergePath("/","/user/*").equals("/user/*");

        assert PathUtil.mergePath("/render/direct/","*").equals("/render/direct/*");
    }

    @Test
    public void test2(){
        StaticMappings.instance().add("/a/","/test/");
    }
}
