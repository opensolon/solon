package webapp.demo2_mvc.util;

import java.io.Serializable;

/**
 * @author noear 2025/7/15 created
 */
public class User implements Serializable {
    String name;

    public String getName() {
        return name;
    }

    public User(String name) {
        this.name = name;
    }
}
