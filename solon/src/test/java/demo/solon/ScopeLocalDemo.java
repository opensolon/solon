package demo.solon;

import org.noear.solon.core.handle.Context;
import org.noear.solon.util.ScopeLocal;

/**
 *
 * @author noear 2025/12/18 created
 *
 */
public class ScopeLocalDemo {
    static ScopeLocal<String> LOCAL = ScopeLocal.newInstance();

    public void case1() {
        LOCAL.with("case1", () -> {
            System.out.println(LOCAL.get());
        });
    }

    public void case2() {
        String rst = LOCAL.with("case2", () -> {
            System.out.println(LOCAL.get());

            return "rst";
        });
    }

    public void case2_1() {
        LOCAL.with("case2_1", () -> {
            System.out.println(LOCAL.get());

            if ("".equals(LOCAL.get())) {
                throw new RuntimeException("xxx");
            }

            return null;
        });
    }

    public void case3(Context newCtx) {
        Context.currentWith(newCtx, () -> {
            Context ctx = Context.current();
        });
    }
}