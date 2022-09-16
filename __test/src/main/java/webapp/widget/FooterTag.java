package webapp.widget;

import org.noear.solon.core.event.EventBus;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class FooterTag extends TagSupport {
    @Override
    public int doStartTag() throws JspException {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("<div>").append("你好 world!").append("</div>");
            pageContext.getOut().write(sb.toString());
        } catch (Exception e) {
            EventBus.push(e);
        }

        return super.doStartTag();
    }

    @Override
    public int doEndTag() throws JspException {
        return super.doEndTag();
    }
}
