package webapp.widget;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class FooterTag extends TagSupport {
    @Override
    public int doStartTag() throws JspException {
        try {
            //当前视图path
            StringBuffer sb = new StringBuffer();
            sb.append("<footer>");
            sb.append("我是自定义标签，FooterTag");
            sb.append("</footer>");
            pageContext.getOut().write(sb.toString());
        }
        catch (Exception e){
            EventBus.push(ex);
        }

        return super.doStartTag();
    }

    @Override
    public int doEndTag() throws JspException {
        return super.doEndTag();
    }
}