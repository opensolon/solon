package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.eval.Soal;
import org.noear.solon.eval.Execable;
import org.noear.solon.test.SolonTest;

/**
 * @author noear
 * @since 3.0
 */
@SolonTest
public class Case21 {
    @Test
    public void test() throws Exception {
        Soal.eval("System.out.println(\"hello word\");");
        Soal.eval("System.out.println(\"hello word\");"); //cached
        Soal.eval("System.out.println(\"hello word---x\");");

        Soal.compile("System.out.println(\"hello word1\");").exec();

        //不推荐
        Execable executable1 = Soal.compile("System.out.println(\"hello word2\");");
        executable1.exec();

        //////////////////////////

        System.out.println(Soal.eval("1+1"));
        System.out.println(Soal.eval("1+1")); //cached
        System.out.println(Soal.eval("1+2"));

        System.out.println(Soal.compile("1+22222").exec());

        //不推荐
        Execable executable2 = Soal.compile("1+1");
        System.out.println(executable2.exec());
    }
}