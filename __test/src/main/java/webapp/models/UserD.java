package webapp.models;

import java.io.Serializable;

public final class UserD implements Serializable {
    final Integer id;
    final String name;

    public UserD(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
