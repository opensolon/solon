package features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.extend.snowflake.impl.SnowflakeId;
import org.noear.solon.test.SolonJUnit5Extension;

/**
 * @author noear 2021/10/9 created
 */
@ExtendWith(SolonJUnit5Extension.class)
public class DemoTest {
    @Test
    public void test(){
        SnowflakeId snowflakeId = new SnowflakeId(1,1);
        IdWorker idWorker = new IdWorker(1,1,1);

        System.out.println(snowflakeId.nextId());
        System.out.println(idWorker.nextId());

        System.out.println(snowflakeId.nextId());
        System.out.println(idWorker.nextId());

        System.out.println(snowflakeId.nextId());
        System.out.println(idWorker.nextId());
    }

    @Test
    public void test2(){
        System.out.println(CloudClient.id().generate());

        System.out.println(CloudClient.idService("demo","demoapi").generate());
    }
}
