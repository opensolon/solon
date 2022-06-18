package features;

import org.junit.Test;
import org.noear.solon.core.util.PathAnalyzer;
import org.noear.solon.core.util.PathUtil;
import org.noear.solon.web.staticfiles.StaticMappings;
import org.noear.solon.web.staticfiles.repository.FileStaticRepository;

public class PathTest {
    @Test
    public void test() {
        assert PathUtil.mergePath("/user/*", "").equals("/user/");
        assert PathUtil.mergePath("", "/user/*").equals("/user/*");
        assert PathUtil.mergePath("/", "/user/*").equals("/user/*");
        assert PathUtil.mergePath("/render/direct/", "*").equals("/render/direct/*");
        assert PathUtil.mergePath("user", "/").equals("/user/");
        assert PathUtil.mergePath("user", "").equals("/user");
        assert PathUtil.mergePath("user/add", "/").equals("/user/add/");
    }

    @Test
    public void test2() {
        StaticMappings.add("/a/", new FileStaticRepository("/test/"));
    }

    @Test
    public void test3() {
        PathAnalyzer pathAnalyzer = PathAnalyzer.get("/demo2/intercept/**");

        assert pathAnalyzer.matches("/demo2/intercept/");
    }

    @Test
    public void test4() {
        PathAnalyzer pathAnalyzer = PathAnalyzer.get("/demo2/intercept/*");

        assert pathAnalyzer.matches("/demo2/intercept/");
    }
}
