package libs;

import org.noear.solon.Solon;

/**
 * @author noear 2024/8/21 created
 */
public class DsTest {
    public static void main(String[] args) {
        Solon.start(DsTest.class, new String[]{"--cfg=test-ds.yml"});

        DataSourceTmp tmp = Solon.context().getBean("db1");
        assert tmp != null;
        assert tmp.getProps().size() > 2;
        assert "root".equals(tmp.getProps().get("username"));
    }
}
