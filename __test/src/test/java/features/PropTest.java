package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.models.CfgItem;

import java.util.List;
import java.util.Map;

/**
 * @author noear 2022/6/11 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class PropTest {

    @Inject("${cfgitems}")
    List<CfgItem> cfgitems;

    @Inject("${cfgmap}")
    Map<String, CfgItem> cfgmap;

    @Inject("${stritems}")
    List<String> stritems;

    @Inject("${strmap}")
    Map<String, String> strmap;

    @Test
    public void test() {
        System.out.println(cfgitems);

        assert cfgitems != null;
        assert cfgitems.size() == 2;
        assert cfgitems.get(0).getId() == 1;

        System.out.println(stritems);

        assert stritems != null;
        assert stritems.size() == 2;
        assert stritems.get(0).equals("id1");
    }

    @Test
    public void test1() {
        System.out.println(cfgmap);

        assert cfgmap != null;
        assert cfgmap.size() == 2;
        assert cfgmap.get("cfg1").getId() == 1;

        System.out.println(strmap);

        assert strmap != null;
        assert strmap.size() == 2;
        assert strmap.get("id1").equals("1");
    }

    @Test
    public void test2() {
        List<String> list1 = Solon.cfg().getBean("stritems", List.class);
        List<String> list2 = Solon.cfg().getList("stritems");

        assert list1 != null;
        assert list2 != null;

        for (int size = list1.size(), i = 0; i < size; i++) {
            assert list1.get(i).equals(list2.get(i));
        }


        assert list1.size() == list2.size();
    }
}
