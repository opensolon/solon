package webapp.demo6_aop;

import org.noear.solon.data.annotation.Tran;

/**
 * @author noear 2023/10/8 created
 */
public class DaoBase<T> {
    @Tran
    public void add(T t){

    }
}
