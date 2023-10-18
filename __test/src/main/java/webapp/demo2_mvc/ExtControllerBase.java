package webapp.demo2_mvc;

import org.noear.solon.annotation.Mapping;

/**
 * @author noear 2023/10/10 created
 */
public class ExtControllerBase<T> {
    @Mapping("hello")
    public String hello(String name){
        return name;
    }

    @Mapping("save")
    public Object save(T entity){
        return entity;
    }
}
