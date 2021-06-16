package test1;

import org.junit.Test;
import org.noear.solon.extend.sessionstate.jwt.JwtUtils;

/**
 * @author noear 2021/6/16 created
 */
public class JwtTest {
    @Test
    public void test(){
        System.out.println(JwtUtils.createKey());
    }
}
