package webapp.models;

import java.io.Serializable;

public final class UserD implements Serializable {
    final Integer id;
    final String name;
    final UserType type;

    public UserD(Integer id, String name, UserType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public Integer id() {
        return id;
    }

    public String name() {
        return name;
    }

    public UserType type() {
        return type;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UserType getType() {
        return type;
    }
}
