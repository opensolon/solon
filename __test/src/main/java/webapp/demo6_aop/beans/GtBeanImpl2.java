package webapp.demo6_aop.beans;

import org.noear.solon.annotation.Component;

/**
 * @author noear 2023/10/10 created
 */
@Component
public class GtBeanImpl2 extends GtBeanBase implements GtBean<String>{
    @Override
    public void save(String s) {

    }
}
