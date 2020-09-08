package test1;

import org.junit.Test;
import org.noear.solon.extend.validation.StringUtils;

public class UtilsTest {
    @Test
    public void test(){
        assert  StringUtils.isNumber("0.1");
        assert  StringUtils.isNumber("1");
        assert  StringUtils.isNumber("-1");
        assert  StringUtils.isNumber("+0.1");
        assert  StringUtils.isNumber("+0.s1") == false;
        assert  StringUtils.isNumber("0");
        assert  StringUtils.isNumber("0. 1") == false;
        assert  StringUtils.isNumber("0..1") == false;
    }

    @Test
    public void test2(){
        assert  StringUtils.isDigits("0.1") == false;
        assert  StringUtils.isDigits("1");
        assert  StringUtils.isDigits("-1") == false;
        assert  StringUtils.isDigits("+0.1") == false;
        assert  StringUtils.isDigits("+0.s1") == false;
        assert  StringUtils.isDigits("0");
        assert  StringUtils.isDigits("0. 1") == false;
        assert  StringUtils.isDigits("0..1") == false;
    }
}
