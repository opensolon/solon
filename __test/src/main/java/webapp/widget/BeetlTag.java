package webapp.widget;

import org.beetl.core.tag.Tag;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import webapp.dso.EmptyService;

/**
 * @author noear 2024/6/2 created
 */
@Component("view:beetlTag")
public class BeetlTag extends Tag {
    @Inject
    EmptyService emptyService;

    @Override
    public void render() {
        assert emptyService != null;
        System.out.println("beetlTag:::ok:::");

        this.doBodyRender();
    }
}
