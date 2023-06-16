package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.models.TestModel;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author noear 2021/5/28 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(App.class)
public class InjectTest {
    @Inject(value = "${username}", autoRefreshed = true)
    String username;


    @Inject("${formattest.text}")
    String formattest;

    @Inject("${formattest.text2}")
    String formattest2;

    @Inject("${formattest.text3}")
    String formattest3;

    @Inject("${formattest.text10}")
    String formattest10;

    @Inject("${formattest.text11}")
    String formattest11;


    @Inject("${inject.set1:1,2,3}")
    Set<String> injectSet1;

    @Inject("${inject.set2:1,2,3}")
    List<Integer> injectSet2;

    @Inject("${inject.set3}")
    Set<Integer> injectSet3;

    @Inject("${inject.set3}")
    Integer[] injectSet3_2;

    @Inject("${inject.set4}")
    Set<Integer> injectSet4;

    @Inject("${inject.set4}")
    Integer[] injectSet4_2;

    @Inject
    Map<String, Object> map;

    @Test
    public void test1() {
        assert "noear".equals(username);

        Solon.cfg().setProperty("username", "xxx");

        assert "xxx".equals(username);

        assert map != null;
        assert map.get("1").equals(1);
    }

    @Test
    public void test2() {
        System.out.println(formattest);
        assert "512m/-/en_US".equals(formattest);

        assert "".equals(formattest2);

        assert "12".equals(formattest3);

        assert Utils.isNotEmpty(formattest10);
        assert formattest10.contains("jdk") || formattest10.contains("Java");

        assert "11".equals(formattest11);
    }

    @Inject
    TestModel testModel;

    @Test
    public void test3() {
        System.out.println(testModel.getTestname());
        assert testModel.getTestname() != null;
    }

    @Test
    public void def_set_test1() {
        assert injectSet1 != null;
        assert injectSet1.size() == 3;
    }

    @Test
    public void def_set_test2() {
        assert injectSet2 != null;
        assert injectSet2.size() == 3;
    }

    @Test
    public void def_set_test3() {
        assert injectSet3 != null;
        assert injectSet3.size() == 3;
    }

    @Test
    public void def_set_test3_2() {
        assert injectSet3_2 != null;
        assert injectSet3_2.length == 3;
    }

    @Test
    public void def_set_test4() {
        assert injectSet4 != null;
        assert injectSet4.size() == 3;
    }

    @Test
    public void def_set_test4_2() {
        assert injectSet4_2 != null;
        assert injectSet4_2.length == 3;
    }
}
