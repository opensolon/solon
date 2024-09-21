package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.eval.CodeSpec;
import org.noear.solon.eval.Evaluator;

/**
 * @author noear 2024/9/20 created
 */
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

        System.out.println(Evaluator.eval(code1));
    }
}
