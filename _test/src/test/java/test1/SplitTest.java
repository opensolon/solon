package test1;

import org.junit.Test;

import java.util.Arrays;
import java.util.Locale;

/**
 * @author noear 2021/11/13 created
 */
public class SplitTest {
    @Test
    public void test() {
        String[] ss;

        ss = "zh_CN".split("_|-");
        System.out.println(Arrays.asList(ss));

        ss = "zh-CN".split("_|-");
        System.out.println(Arrays.asList(ss));

        ss = "zh_CN-HANS".split("_|-");
        System.out.println(Arrays.asList(ss));
    }

    @Test
    public void test2() {
        String[] ss;

        ss = "zh".split("_|-");
        System.out.println(new Locale(ss[0]));

        ss = "zh_CN".split("_|-");
        System.out.println(new Locale(ss[0], ss[1]));

        ss = "zh-cn".split("_|-");
        System.out.println(new Locale(ss[0], ss[1]));

        ss = "zh_CN-HANS".split("_|-");
        System.out.println(new Locale(ss[0], ss[1], ss[2]));
    }
}
