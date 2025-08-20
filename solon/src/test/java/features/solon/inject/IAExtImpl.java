package features.solon.inject;

import org.noear.solon.annotation.Managed;

/**
 * @author noear 2024/12/17 created
 */
@Managed
public class IAExtImpl implements IAExt {
    @Override
    public void b() {
        System.out.println("b-2");
    }

    @Override
    public void a() {
        System.out.println("a-2");
    }
}
