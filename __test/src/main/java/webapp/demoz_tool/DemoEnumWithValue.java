package webapp.demoz_tool;

public enum DemoEnumWithValue {
    A(1, "a"),
    B(2, "b");

    private Integer value;
    private String name;

    private DemoEnumWithValue(Integer value, String name) {
        this.value = value;
        this.name = name;
    }
}
