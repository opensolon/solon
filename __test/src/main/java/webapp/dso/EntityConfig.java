package webapp.dso;

import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;

import java.util.List;
import java.util.Map;

/**
 * @author noear 2021/9/27 created
 */
@Inject("${app.dict}")
@Configuration
public class EntityConfig {
    public String name;
    public Integer age;
    //@Inject("${app.dict.codes}")//不需要分块注入了
    public Map<String,String> codes;
    //@Inject("${app.dict.likes}")//不需要分块注入了
    public List<String> likes;

    @Override
    public String toString() {
        return "EntityConfig{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", codes=" + codes +
                ", likes=" + likes +
                '}';
    }


    @Init
    public void init(){
        System.out.println("我是5号");
    }
}
