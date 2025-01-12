/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package labs.test3;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author noear 2022/8/10 created
 */
public class RegTest {

    //981 3 4981341,234,qef,898一二三四五六12398,四五 六123412,9 8 1 3d4d 9,0098088,qweofijqwfe,yi 2 shan yi yi yi lu wu wu wu,986 one 12,十10381one
    //([\d零一二三四五六七八九十]+\w*|\s|yi|er|san|sang|shan|shang|si|shi|wu|lu|qi|ba|jiu|shi|ling|zero|one|two|three|four|five|six|seven|eight|nine|ten){7,}

    //表达式
    static String expr = "([\\d零一二三四五六七八九十①②③④⑤⑥⑦⑧⑨壹贰叁肆伍陆柒捌玖]+\\w*|\\s|yi|er|san|sang|shan|shang|si|shi|wu|lu|qi|ba|jiu|shi|ling|zero|one|two|three|four|five|six|seven|eight|nine|ten){7,}";
    //表达式预编译
    static Pattern exprPattern = Pattern.compile(expr, Pattern.CASE_INSENSITIVE);

    String[] text = {"太阳981 3 4981341中国人",
            "234",
            "qef",
            "898一二三四五六12398",
            "四五 六123412",
            "9 8 1 3d4d 9",
            "0098088",
            "qweofijqwfe",
            "yi 2 shan yi yi yi lu wu wu wu",
            "986 one 12",
            "十10381one",
            "          ",
            "     "};

    @Test
    public void test() {
        testDo(0, true);
        testDo(1, false);
        testDo(2, false);
        testDo(3, true);
        testDo(4, true);
        testDo(5, true);
        testDo(6, true);
        testDo(7, false);
        testDo(8, true);
        testDo(9, true);
        testDo(10, true);
        testDo(11, true);
        testDo(12, false);
    }

    private void testDo(int idx, boolean expect) {
        String val = text[idx];
        boolean rst = exprPattern.matcher(val).find();

        System.out.println(idx + ": " + val + " === " + rst);

        assert rst == expect;
    }


    @Test
    public void test2() {
        //预热
        exprPattern.matcher(text[1]).find();
        exprPattern.matcher(text[1]).find();

        //跑分
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            exprPattern.matcher(text[1]).find();
        }
        System.out.println("耗时：" + (System.currentTimeMillis() - start) + "ms");
    }

    @Test
    public void test3() {
        String val = text[0];
        Matcher mat = exprPattern.matcher(val);
        boolean rst = mat.find();

        System.out.println(0 + ": " + val + " === " + rst);
        System.out.println(mat.group());
        System.out.println(val.replace(mat.group(),"<t>"+mat.group()+"</t>"));
    }
}
