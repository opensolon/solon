/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.util.ClassUtil;
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

        Collection<Class<?>> classes = ClassUtil.scanClasses("webapp.**.cache.*.class");
        System.out.println(classes.size());
        assert classes.size() >= 2;

        classes = ClassUtil.scanClasses("webapp.**.cache.*");
        System.out.println(classes.size());
        assert classes.size() >= 2;


        classes = ClassUtil.scanClasses("webapp.dso.cache.*");
        System.out.println(classes.size());
        assert classes.size() >= 2;

        classes = ClassUtil.scanClasses("webapp.dso.cache.*.class");
        System.out.println(classes.size());
        assert classes.size() >= 2;

        classes = ClassUtil.scanClasses("webapp.dso.*.class");
        System.out.println(classes.size());
        assert classes.size() >= 16;

        classes = ClassUtil.scanClasses("webapp.dso.*");
        System.out.println(classes.size());
        assert classes.size() >= 16;

        classes = ClassUtil.scanClasses("webapp.dso.*.*.class");
        System.out.println(classes.size());
        assert classes.size() >= 8;
    }

    @Test
    public void resolveClasses2(){
        //
        //如果是apt代理，会找到更多的类
        //

        Collection<Class<?>> classes;

        classes = ClassUtil.scanClasses("webapp.**.cache.*");
        System.out.println(classes.size());
        assert classes.size() >= 2;


        classes = ClassUtil.scanClasses("webapp.dso.cache.*");
        System.out.println(classes.size());
        assert classes.size() >= 2;

        classes = ClassUtil.scanClasses("webapp.dso.*");
        System.out.println(classes.size());
        assert classes.size() >= 16;

        classes = ClassUtil.scanClasses("webapp.dso.*.*");
        System.out.println(classes.size());
        assert classes.size() >= 8;
    }


    @Test
    public void resolveClasses3(){
        //
        //如果是apt代理，会找到更多的类
        //

        Collection<Class<?>> classes;

        classes = ClassUtil.scanClasses("webapp.**.cache.*Server");
        System.out.println(classes.size());
        assert classes.size() == 1;


        classes = ClassUtil.scanClasses("webapp.*.cache");
        System.out.println(classes.size());
        assert classes.size() >= 2;
    }

    @Test
    public void resolveClasses4(){
        //
        //如果是apt代理，会找到更多的类
        //

        Collection<Class<?>> classes;

        classes = ClassUtil.scanClasses("webapp.dso.cache");
        System.out.println(classes.size());
        assert classes.size() >= 2;


        classes = ClassUtil.scanClasses("webapp.dso.cache.OathServer");
        System.out.println(classes.size());
        assert classes.size() == 1;
    }



    @Test
    public void resolveClasses5(){
        Collection<String> resSet;

        resSet = ResourceUtil.scanResources("classpath*:props_test/dev-*.yml");
        System.out.println(resSet.size());
        assert resSet.size() == 2;
    }
}
