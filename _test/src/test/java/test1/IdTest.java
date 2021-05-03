package test1;

import cn.hutool.core.date.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.test.SolonJUnit4ClassRunner;

/**
 * @author noear 2021/5/3 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
public class IdTest {
    @Test
    public void time1(){
        DateTime dateTime = DateTime.of("2015-01-01 00:00:00","yyyy-MM-dd HH:mm:ss");
        System.out.println(dateTime.getTime());
    }

    @Test
    public void time2(){
        DateTime dateTime = DateTime.of("2020-01-01 00:00:00","yyyy-MM-dd HH:mm:ss");
        System.out.println(dateTime.getTime());
    }

    @Test
    public void id() {
        //838528494260015104
        //177092937095925760
        System.out.println(CloudClient.id().generate());

        long start = System.currentTimeMillis();

        for (int i = 0; i < 100000; i++) {
            CloudClient.id().generate();
        }

        System.out.println("times: " + (System.currentTimeMillis() - start) + "ms");
    }
}
