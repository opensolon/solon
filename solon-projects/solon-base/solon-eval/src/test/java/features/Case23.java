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
public class Case23 {
    @Test
    public void test() throws Exception {
        CodeSpec code1 = new CodeSpec("import java.util.HashMap;\n" +
                "    import java.util.Collection;\n" +

                "    class Demo extends HashMap<String, Object> {}\n" +
                "\n" +
                "    Demo demo = new Demo();\n" +

                "    if(demo instanceof Collection){\n" +
                "        return demo.size();\n" +
                "    } else{\n" +
                "        return 0;\n" +
                "    }") //name 为外部参数
                .returnType(Object.class);

        System.out.println(Soal.eval(code1));
    }
}
