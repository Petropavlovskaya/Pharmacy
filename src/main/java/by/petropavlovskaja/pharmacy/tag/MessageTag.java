package by.petropavlovskaja.pharmacy.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Class for custom tag. Extending {@link SimpleTagSupport}
 */
public class MessageTag extends SimpleTagSupport {
    /**
     * Property - message
     */
    private String msg;
    /**
     * Property - string writer
     */
    StringWriter sw = new StringWriter();

    /**
     * The method for setting the message for custom tag
     *
     * @param msg - a message
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * The override method {@link SimpleTagSupport#doTag()}
     */
    @Override
    public void doTag() throws JspException, IOException {
        if (msg != null) {
            /* Use message from attribute */
            JspWriter out = getJspContext().getOut();
            out.println(msg);
        } else {
            /* use message from the body */
            getJspBody().invoke(sw);
            getJspContext().getOut().println(sw.toString());
        }
    }
}
