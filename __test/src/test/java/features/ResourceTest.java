package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.util.Collection;

/**
 * @author noear 2023/3/19 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class ResourceTest {
    @Test
    public void scanResources(){
        Collection<String> paths = ResourceUtil.scanResources("static_test/**/dir2/*.htm");
        System.out.println(String.join(",", paths));
        assert paths.size() == 1;

        paths = ResourceUtil.scanResources("static_test/dir1/*/b.htm");
        System.out.println(String.join(",", paths));
        assert paths.size() == 1;

        paths = ResourceUtil.scanResources("static_test/dir1/*/*.htm");
        System.out.println(String.join(",", paths));
        assert paths.size() == 1;

        paths = ResourceUtil.scanResources("static_test/dir1/dir2/*.htm");
        System.out.println(String.join(",", paths));
        assert paths.size() == 1;

        paths = ResourceUtil.scanResources("static_test/**/*.htm");
        System.out.println(String.join(",", paths));
        assert paths.size() == 2;
    }
}
