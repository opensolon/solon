package demo.config.snack4;

import org.noear.snack4.ONode;
import org.noear.snack4.codec.EncodeContext;
import org.noear.snack4.codec.ObjectEncoder;
import org.noear.solon.extend.impl.PropsConverterExt;

public class PropsTest2 {
    public void case2() {
        PropsConverterExt.getOptions()
                .addEncoder(Case2Do.class, new ObjectEncoder<Case2Do>() {
                    @Override
                    public ONode encode(EncodeContext ctx, Case2Do value, ONode target) {
                        return null;
                    }
                });
    }

    public static class Case2Do {

    }
}