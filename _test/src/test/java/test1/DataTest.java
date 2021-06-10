package test1;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author noear 2021/6/10 created
 */
public class DataTest {
    @Test
    public void test(){
        System.out.println(LocalDateTime.now());

        System.out.println(LocalDateTime.now(ZoneOffset.ofHours(0)));
    }
}
