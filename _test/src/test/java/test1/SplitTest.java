package test1;

import org.junit.Test;

/**
 * @author noear 2021/11/13 created
 */
public class SplitTest {
    @Test
    public void test(){
        System.out.println("zh_CN".split("_|-"));
        System.out.println("zh-CN".split("_|-"));
        System.out.println("zh_CN-HANS".split("_|-"));
    }
}
