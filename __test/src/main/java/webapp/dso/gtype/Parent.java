package webapp.dso.gtype;

import org.noear.solon.annotation.Inject;

/**
 * @author noear 2022/1/17 created
 */
public abstract class Parent<T extends Service> {
    @Inject
    protected T s;

    public int hello() {
        return this.s.hello();
    }
}
