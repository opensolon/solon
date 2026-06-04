package demo.config.snack4;

import org.noear.snack4.ONode;
import org.noear.snack4.annotation.ONodeAttr;
import org.noear.snack4.codec.DecodeContext;
import org.noear.snack4.codec.ObjectDecoder;
import org.noear.solon.extend.impl.PropsConverterExt;

public class PropsTest1 {
    public void case1() {
        PropsConverterExt.getOptions();
    }

    //@ONodeAttr(creator = Xxx.class) // 可能根据情况使用：生成器，编码器，解码器
    @ONodeAttr(decoder = Case1DoDecoder.class)
    public static class Case1Do {

    }

    public static class Case1DoDecoder implements ObjectDecoder<Case1Do> {
        @Override
        public Case1Do decode(DecodeContext<Case1Do> ctx, ONode node) {
            return null;
        }
    }
}