package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.eval.CodeSpec;
import org.noear.solon.eval.ParamSpec;

/**
 * @author noear 2024/9/20 created
 */
public class HashTest {
    @Test
    public void test() throws Exception {
        System.out.println(new CodeSpec("1+1").hashCode());
        System.out.println(new CodeSpec("1+1").hashCode());
        System.out.println("-------------");
        assert new CodeSpec("1+1").hashCode() == new CodeSpec("1+1").hashCode();

        System.out.println(new CodeSpec("1+1").parameters(new ParamSpec("name", String.class)).hashCode());
        System.out.println(new CodeSpec("1+1").parameters(new ParamSpec("name", String.class)).hashCode());
        System.out.println("-------------");
        assert new CodeSpec("1+1").parameters(new ParamSpec("name", String.class)).hashCode() == new CodeSpec("1+1").parameters(new ParamSpec("name", String.class)).hashCode();

        System.out.println(new ParamSpec("name", String.class).hashCode());
        System.out.println(new ParamSpec("name", String.class).hashCode());
        assert new ParamSpec("name", String.class).hashCode() == new ParamSpec("name", String.class).hashCode();
    }
}