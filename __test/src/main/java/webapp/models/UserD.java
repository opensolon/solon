package webapp.models;

public final class UserD {
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
}
