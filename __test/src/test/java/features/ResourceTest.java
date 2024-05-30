package features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;

/**
 * @author noear 2023/3/19 created
 */
@SolonTest(App.class)
public class ResourceTest {
    @Test
    public void scanResources() {
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

    @Test
    public void scanResources2() {
        Collection<String> paths = ResourceUtil.scanResources("/static_test/**/dir2/*.htm");
        System.out.println(String.join(",", paths));
        assert paths.size() == 1;

        paths = ResourceUtil.scanResources("/static_test/dir1/*/b.htm");
        System.out.println(String.join(",", paths));
        assert paths.size() == 1;

        paths = ResourceUtil.scanResources("/static_test/dir1/*/*.htm");
        System.out.println(String.join(",", paths));
        assert paths.size() == 1;

        paths = ResourceUtil.scanResources("/static_test/dir1/dir2/*.htm");
        System.out.println(String.join(",", paths));
        assert paths.size() == 1;

        paths = ResourceUtil.scanResources("/static_test/**/*.htm");
        System.out.println(String.join(",", paths));
        assert paths.size() == 2;
    }

    @Test
    public void getResources() throws IOException {
        Enumeration<URL> url = ResourceUtil.getResources("app.yml");
        assert url != null;
        System.out.println(url);

        url = ResourceUtil.getResources("/app.yml");
        assert url != null;
        System.out.println(url);
    }

    @Test
    public void getResource() {
        URL url = ResourceUtil.getResource("app.yml");
        assert url != null;
        System.out.println(url);

        url = ResourceUtil.getResource("/app.yml");
        assert url != null;
        System.out.println(url);
    }


    @Test
    public void findResource() {
        URL url = ResourceUtil.findResource("classpath:app.yml");
        assert url != null;
        System.out.println(url);

        url = ResourceUtil.findResource("classpath:/app.yml");
        assert url != null;
        System.out.println(url);
    }
}
