package features.sessionstate.redisson;

/**
 * @author noear 2024/11/29 created
 */
public class StateDo {
    String id;
    long t1;
    long t2;

    public String getId() {
        return id;
    }

    public long getT1() {
        return t1;
    }

    public long getT2() {
        return t2;
    }

    @Override
    public String toString() {
        return "StateDo{" +
                "id='" + id + '\'' +
                ", t1=" + t1 +
                ", t2=" + t2 +
                '}';
    }
}
