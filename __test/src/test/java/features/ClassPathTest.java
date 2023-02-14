package features;

import org.junit.Test;
import org.noear.solon.core.util.PathAnalyzer;
import org.noear.solon.core.util.PathUtil;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.web.staticfiles.StaticMappings;
import org.noear.solon.web.staticfiles.repository.FileStaticRepository;

import java.util.Collection;

public class ClassPathTest {
    @Test
    public void resolvePaths(){
        Collection<String> paths = ResourceUtil.resolvePaths("static_test/**/dir2/*.htm");
        System.out.println(String.join(",", paths));
        assert paths.size() == 1;

        paths = ResourceUtil.resolvePaths("static_test/dir1/*/b.htm");
        System.out.println(String.join(",", paths));
        assert paths.size() == 1;

        paths = ResourceUtil.resolvePaths("static_test/dir1/*/*.htm");
        System.out.println(String.join(",", paths));
        assert paths.size() == 1;

        paths = ResourceUtil.resolvePaths("static_test/dir1/dir2/*.htm");
        System.out.println(String.join(",", paths));
        assert paths.size() == 1;

        paths = ResourceUtil.resolvePaths("static_test/**/*.htm");
        System.out.println(String.join(",", paths));
        assert paths.size() == 2;
    }

    @Test
    public void resolveClasses(){
        Collection<Class<?>> classes = ResourceUtil.resolveClasses("webapp.**.cache.*.class");
        System.out.println(classes.size());
        assert classes.size() == 2;

        classes = ResourceUtil.resolveClasses("webapp.**.cache");
        System.out.println(classes.size());
        assert classes.size() == 2;


        classes = ResourceUtil.resolveClasses("webapp.dso.cache");
        System.out.println(classes.size());
        assert classes.size() == 2;

        classes = ResourceUtil.resolveClasses("webapp.dso.cache.*.class");
        System.out.println(classes.size());
        assert classes.size() == 2;

        classes = ResourceUtil.resolveClasses("webapp.dso.*.class");
        System.out.println(classes.size());
        assert classes.size() == 16;

        classes = ResourceUtil.resolveClasses("webapp.dso");
        System.out.println(classes.size());
        assert classes.size() == 16;

        classes = ResourceUtil.resolveClasses("webapp.dso.*.*.class");
        System.out.println(classes.size());
        assert classes.size() == 8;
    }

    @Test
    public void resolveClasses2(){
        Collection<Class<?>> classes;

        classes = ResourceUtil.resolveClasses("webapp.**.cache.*");
        System.out.println(classes.size());
        assert classes.size() == 2;


        classes = ResourceUtil.resolveClasses("webapp.dso.cache.*");
        System.out.println(classes.size());
        assert classes.size() == 2;

        classes = ResourceUtil.resolveClasses("webapp.dso.*");
        System.out.println(classes.size());
        assert classes.size() == 16;

        classes = ResourceUtil.resolveClasses("webapp.dso.*.*");
        System.out.println(classes.size());
        assert classes.size() == 8;
    }
}
