package GenericInjectTest;

import org.noear.solon.annotation.Inject;

/**
 * @author noear 2022/10/27 created
 */
public abstract class BaseController<T> {
    @Inject
    private T service;

    public T getService() {
        return service;
    }
}
