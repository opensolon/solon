package webapp.demo7_test;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.data.dynamicds.DynamicDs;
import org.noear.solon.data.dynamicds.DynamicDsKey;

/**
 * @author noear 2023/9/1 created
 */
@Component
public class DynamicService {
    @DynamicDs("db_rock1")
    public String test1() throws Exception {
        System.out.println("ds===" + DynamicDsKey.getCurrent());
        return DynamicDsKey.getCurrent();
    }


    @DynamicDs("db_rock2")
    @Mapping("/test2")
    public String test2() throws Exception {
        System.out.println("ds===" + DynamicDsKey.getCurrent());
        return DynamicDsKey.getCurrent();
    }


    @DynamicDs
    @Mapping("/test3")
    public String test3() throws Exception {
        System.out.println("ds===" + DynamicDsKey.getCurrent());
        return DynamicDsKey.getCurrent();
    }
}
