package webapp.models;

/**
 * @author noear 2023/7/17 created
 */
public enum CatType {
    Demo1(1, "demo1"),
    Demo2(2, "demo2");
    public int code;
    public String title;

    CatType(int code, String title) {
        this.code = code;
        this.title = title;
    }
}
