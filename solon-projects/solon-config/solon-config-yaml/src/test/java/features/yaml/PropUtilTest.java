package features.yaml;

import org.junit.jupiter.api.Test;
import org.noear.solon.Utils;

import java.util.Properties;

/**
 * @author noear 2025/5/29 created
 */
public class PropUtilTest {
    @Test
    public void jsonCase1() {
        Properties properties = Utils.buildProperties("{\"a\":\"a\",\"b\":\"b\"}");
        System.out.println(properties);
        assert "{b=b, a=a}".equals(properties.toString());
    }

    @Test
    public void jsonCase2() {
        Properties properties = Utils.buildProperties("[1,2]");
        System.out.println(properties);
        assert "{[0]=1, [1]=2}".equals(properties.toString());
    }

    @Test
    public void yamlCase1() {
        Properties properties = Utils.buildProperties("a: a\n" +
                "b: b");
        System.out.println(properties);
        assert "{b=b, a=a}".equals(properties.toString());
    }

    @Test
    public void yamlCase2() {
        Properties properties = Utils.buildProperties("- 1\n" +
                "- 2");
        System.out.println(properties);
        assert "{[0]=1, [1]=2}".equals(properties.toString());
    }

    @Test
    public void propCase1() {
        Properties properties = Utils.buildProperties("a=a\n" +
                "b=b");
        System.out.println(properties);
        assert "{b=b, a=a}".equals(properties.toString());
    }

    @Test
    public void propCase2() {
        Properties properties = Utils.buildProperties("[0]=1\n" +
                "[1]=2");
        System.out.println(properties);
        assert "{[0]=1, [1]=2}".equals(properties.toString());
    }
}
