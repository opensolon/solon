package labs;

import org.noear.solon.Solon;
import org.noear.solon.core.mvc.ActionExecuteHandlerDefault;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.wrap.ParamWrap;

/**
 * @author noear 2023/7/15 created
 */
public class ExeTest {
    public static void main(String[] args) {
        Solon.start(ExeTest.class, args, app -> {
            app.chainManager().defExecuteHandler(new ActionExecuteHandlerDefault() {
                @Override
                protected Object changeValueDo(Context ctx, ParamWrap p, String name, Class<?> type, String value) {
                    if(type.isEnum()){

                    }

                    return super.changeValueDo(ctx, p, name, type, value);
                }
            });
        });
    }
}
