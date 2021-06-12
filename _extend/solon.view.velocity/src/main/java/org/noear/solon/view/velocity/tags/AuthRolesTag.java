package org.noear.solon.view.velocity.tags;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.ASTBlock;
import org.apache.velocity.runtime.parser.node.ASTStringLiteral;
import org.apache.velocity.runtime.parser.node.Node;
import org.noear.solon.Utils;
import org.noear.solon.auth.AuthUtil;
import org.noear.solon.auth.annotation.Logical;
import org.noear.solon.auth.tags.Constants;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * 授权给角色
 *
 * @author noear
 * @since 1.4
 */
public class AuthRolesTag extends Directive {
    @Override
    public String getName() {
        return Constants.TAG_authRoles;
    }

    @Override
    public int getType() {
        return BLOCK;
    }

    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        int attrNum = node.jjtGetNumChildren();

        if (attrNum == 0) {
            return true;
        }

        ASTBlock innerBlock = null;
        List<String> attrList = new ArrayList<>();
        for (int i = 0; i < attrNum; i++) {
            Node n1 = node.jjtGetChild(i);
            if (n1 instanceof ASTStringLiteral) {
                attrList.add((String) n1.value(context));
                continue;
            }

            if (n1 instanceof ASTBlock) {
                innerBlock = (ASTBlock) n1;
            }
        }

        if (innerBlock == null || attrList.size() == 0) {
            return true;
        }


        String nameStr = attrList.get(0);
        String logicalStr = null;
        if (attrList.size() > 1) {
            logicalStr = attrList.get(1);
        }

        if (Utils.isNotEmpty(nameStr)) {

            String[] names = nameStr.split(",");
            if (names.length > 0) {
                if (AuthUtil.verifyRoles(names, Logical.of(logicalStr))) {
                    StringWriter content = new StringWriter();
                    innerBlock.render(context, content);
                    writer.write(content.toString());
                }
            }
        }

        return true;
    }
}
