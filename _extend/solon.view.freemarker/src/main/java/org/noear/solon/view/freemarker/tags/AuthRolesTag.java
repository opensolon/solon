package org.noear.solon.view.freemarker.tags;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import org.noear.solon.Utils;
import org.noear.solon.auth.AuthUtil;
import org.noear.solon.auth.annotation.Logical;
import org.noear.solon.auth.tags.AuthConstants;
import org.noear.solon.core.NvMap;

import java.io.IOException;
import java.util.Map;

/**
 * @author noear
 * @since 1.4
 */
public class AuthRolesTag implements TemplateDirectiveModel {
    @Override
    public void execute(Environment env, Map map, TemplateModel[] templateModels, TemplateDirectiveBody body) throws TemplateException, IOException {
        NvMap mapExt = new NvMap(map);

        String nameStr = mapExt.get(AuthConstants.ATTR_name);
        String logicalStr = mapExt.get(AuthConstants.ATTR_logical);

        if (Utils.isEmpty(nameStr)) {
            return;
        }

        String[] names = nameStr.split(",");

        if (names.length == 0) {
            return;
        }

        if (AuthUtil.verifyRoles(names, Logical.of(logicalStr))) {
            body.render(env.getOut());
        }
    }
}
