package demo.expr;

import org.junit.jupiter.api.Test;
import org.noear.solon.expression.snel.SnEL;

/**
 * @author noear 2025/3/13 created
 */
public class DemoText {
    @Test
    public void case1(){
        System.out.println(SnEL.eval("'hello world!'"));
    }
}
