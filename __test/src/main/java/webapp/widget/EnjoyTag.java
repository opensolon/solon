package webapp.widget;

import com.jfinal.template.Directive;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Singleton;
import webapp.dso.EmptyService;

/**
 * @author noear 2024/6/2 created
 */
@Singleton(false)
@Component("view:enjoyTag")
public class EnjoyTag extends Directive {
    @Inject
    EmptyService emptyService;

    @Override
    public void exec(Env env, Scope scope, Writer writer) {
        assert emptyService != null;
        System.out.println("enjoyTag:::ok:::");

        stat.exec(env, scope, writer);
    }

    @Override
    public boolean hasEnd() {
        return true;
    }
}
