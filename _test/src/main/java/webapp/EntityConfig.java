package webapp;

import org.noear.solon.annotation.Configuration;
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
    @Inject("${app.dict.codes}")
    public Map<String,String> codes;
    @Inject("${app.dict.likes}")
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
}
