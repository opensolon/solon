package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.eval.Evaluator;
import org.noear.solon.eval.Execable;

/**
 * @author noear
 * @since 1.1
 */
public class Case21 {
    @Test
    public void test() throws Exception {
        Evaluator.eval("System.out.println(\"hello word\");");
        Evaluator.eval("System.out.println(\"hello word\");"); //cached
        Evaluator.eval("System.out.println(\"hello word---x\");");

        Evaluator.compile("System.out.println(\"hello word1\");").exec();

        //不推荐
        Execable executable1 = Evaluator.compile("System.out.println(\"hello word2\");");
        executable1.exec();

        //////////////////////////

        System.out.println(Evaluator.eval("1+1"));
        System.out.println(Evaluator.eval("1+1")); //cached
        System.out.println(Evaluator.eval("1+2"));

        System.out.println(Evaluator.compile("1+22222").exec());

        //不推荐
        Execable executable2 = Evaluator.compile("1+1");
        System.out.println(executable2.exec());
    }
}