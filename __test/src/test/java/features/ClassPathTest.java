package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.util.ResourceUtil;

import java.util.Collection;

public class ClassPathTest {
    @Test
    public void resolvePaths(){
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
    public void resolveClasses(){
        //
        //如果是apt代理，会找到更多的类
        //

        Collection<Class<?>> classes = ResourceUtil.scanClasses("webapp.**.cache.*.class");
        System.out.println(classes.size());
        assert classes.size() >= 2;

        classes = ResourceUtil.scanClasses("webapp.**.cache.*");
        System.out.println(classes.size());
        assert classes.size() >= 2;


        classes = ResourceUtil.scanClasses("webapp.dso.cache.*");
        System.out.println(classes.size());
        assert classes.size() >= 2;

        classes = ResourceUtil.scanClasses("webapp.dso.cache.*.class");
        System.out.println(classes.size());
        assert classes.size() >= 2;

        classes = ResourceUtil.scanClasses("webapp.dso.*.class");
        System.out.println(classes.size());
        assert classes.size() >= 16;

        classes = ResourceUtil.scanClasses("webapp.dso.*");
        System.out.println(classes.size());
        assert classes.size() >= 16;

        classes = ResourceUtil.scanClasses("webapp.dso.*.*.class");
        System.out.println(classes.size());
        assert classes.size() >= 8;
    }

    @Test
    public void resolveClasses2(){
        //
        //如果是apt代理，会找到更多的类
        //

        Collection<Class<?>> classes;

        classes = ResourceUtil.scanClasses("webapp.**.cache.*");
        System.out.println(classes.size());
        assert classes.size() >= 2;


        classes = ResourceUtil.scanClasses("webapp.dso.cache.*");
        System.out.println(classes.size());
        assert classes.size() >= 2;

        classes = ResourceUtil.scanClasses("webapp.dso.*");
        System.out.println(classes.size());
        assert classes.size() >= 16;

        classes = ResourceUtil.scanClasses("webapp.dso.*.*");
        System.out.println(classes.size());
        assert classes.size() >= 8;
    }
}
