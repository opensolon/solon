package test1;

import org.junit.Test;

/**
 * @author noear 2021/7/28 created
 */
public class GroupTest {
    @Test
    public void test() {
        assert "k::v".split("::").length == 2;
        assert "k::v".split(":").length == 3;
    }
}
