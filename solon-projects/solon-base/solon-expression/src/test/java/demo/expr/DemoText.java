package demo.expr;

import org.junit.jupiter.api.Test;
import org.noear.solon.expression.snel.SnEL;

/**
 * @author noear 2025/3/13 created
 */
public class DemoText {
    @Test
    public void debug() {
        System.out.println(SnEL.eval("[1,2,3]"));
    }

    @Test
    public void case1() {
        System.out.println(SnEL.eval("'hello world!'"));
    }

    @Test
    public void case2() {
        System.out.println(SnEL.eval("1"));
        System.out.println(SnEL.eval("'solon'"));
        System.out.println(SnEL.eval("true"));
        System.out.println(SnEL.eval("[1,2,3]"));
    }
}
