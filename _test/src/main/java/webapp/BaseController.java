package webapp;

import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Render;

/**
 * @author noear 2021/6/13 created
 */
public class BaseController implements Render {
    @Override
    public void render(Object data, Context ctx) throws Throwable {
        if(ctx.getRendered()){
            return;
        }

        ctx.setRendered(true);

        if(data instanceof Throwable){
            ctx.output(Utils.throwableToString((Throwable) data));
        }else if(data instanceof String){
            ctx.output((String) data);
        }else{
            ctx.outputAsJson(ONode.stringify(data));
        }
    }
}
