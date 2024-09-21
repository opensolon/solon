package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.eval.CodeSpec;
import org.noear.solon.eval.Soal;
import org.noear.solon.test.SolonTest;


/**
 * @author noear
 * @since 3.0
 */
@SolonTest
public class Case21_2 {
    @Test
    public void test() throws Exception {
        // 数学运算 (Long)
        String exp1 = "1+2+3";
        Integer result = (Integer) Soal.eval(exp1);
        System.out.println(result); // 6

        // 数学运算 (Double)
        String exp4 = "1.1+2.2+3.3";
        Double result2 = (Double) Soal.eval(exp4);
        System.out.println(result2); // 6.6


        // 包含关系运算和逻辑运算
        String exp2 = "(1>0||0<1)&&1!=0";
        System.out.println(Soal.eval(exp2)); // true


        // 三元运算
        String exp3 = "4 > 3 ? \"4 > 3\" : 999";
        System.out.println(Soal.eval(exp3)); // 4 > 3


        CodeSpec exp5 = new CodeSpec("Math.min(1,2)").imports(Math.class);
        System.out.println(Soal.eval(exp5));

        Soal.eval(new CodeSpec("System.out.println(Math.min(11,21));").imports(Math.class));
    }
}
