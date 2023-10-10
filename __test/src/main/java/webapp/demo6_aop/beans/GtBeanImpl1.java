package webapp.demo6_aop.beans;

import org.noear.solon.annotation.Component;

/**
 * @author noear 2023/10/10 created
 */
@Component
public class GtBeanImpl1 extends GtBeanBase implements GtBean<Integer>{
    @Override
    public void save(Integer i) {

    }
}
