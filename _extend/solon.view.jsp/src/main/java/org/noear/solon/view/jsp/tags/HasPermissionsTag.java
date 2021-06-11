package org.noear.solon.view.jsp.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

/**
 * @author noear 2021/6/11 created
 */
public class HasPermissionsTag extends TagSupport {
    @Override
    public int doStartTag() throws JspException {
        try {
            pageContext.getOut().write("hi");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return super.doStartTag();
    }
}
