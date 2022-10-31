package test1;

import org.junit.Test;

import java.util.regex.Pattern;

public class ExprTest {
    @Test
    public void test(){
        Pattern pp= Pattern.compile("^[a-z]([a-z0-9]*[-_]?[a-z0-9]+)*@([a-z0-9]*[-_]?[a-z0-9]+)+[\\.][a-z]{2,3}([\\.][a-z]{2})?$");

        assert pp.matcher("service@xsoftlab.net").find();
        assert pp.matcher("noear@live.com.cn").find();
        assert pp.matcher("noe120ar@li0ve.com.cn").find();
        assert pp.matcher("db_db@li0ve.com.cn").find();
        assert pp.matcher("as-df@li0ve.com.cn").find();
        assert pp.matcher("as-df@li-ve.com.cn").find();
    }
}
