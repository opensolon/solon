package features;

import org.junit.Test;
import org.noear.solon.cloud.extend.snowflake.impl.SnowflakeId;

/**
 * @author noear 2021/10/9 created
 */
public class DemoTest {
    @Test
    public void test(){
        SnowflakeId snowflakeId = new SnowflakeId(1,1);
        IdWorker idWorker = new IdWorker(1,1,1);

        //System.out.println(snowflakeId.nextId());
        System.out.println(idWorker.nextId());
    }
}
