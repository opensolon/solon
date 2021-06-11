package org.noear.solon.view.velocity.tags;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.noear.solon.Utils;
import org.noear.solon.annotation.Component;
import org.noear.solon.auth.AuthUtil;
import org.noear.solon.auth.annotation.Logical;

import java.io.IOException;
import java.io.Writer;

/**
 * @author noear
 * @since 1.4
 */
@Component("view:hasPermission")
public class HasPermissionTag extends Directive {
    @Override
    public String getName() {
        return "hasPermission";
    }

    @Override
    public int getType() {
        return BLOCK;
    }

    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        int attrNum = node.jjtGetNumChildren();

        if (attrNum < 0) {
            return false;
        }

        String nameStr = (String) node.jjtGetChild(0).value(context);
        String logicalStr = null;

        if (attrNum > 1) {
            logicalStr = (String) node.jjtGetChild(1).value(context);
        }

        if (Utils.isEmpty(nameStr)) {
            return false;
        }

        String[] names = nameStr.split(",");

        if (names.length == 0) {
            return false;
        }

        if (AuthUtil.verifyPermissions(names, Logical.of(logicalStr))) {
            return true;
        } else {
            return false;
        }
    }
}
