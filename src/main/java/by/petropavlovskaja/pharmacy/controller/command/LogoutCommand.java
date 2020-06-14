package by.petropavlovskaja.pharmacy.controller.command;

import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.controller.session.SessionContext;
import by.petropavlovskaja.pharmacy.model.account.AccountRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;

public class LogoutCommand implements IFrontCommand {
    private static Logger logger = LoggerFactory.getLogger(LogoutCommand.class);
    private Cookie cookie;

    private static class LogoutHolder {
        public static final LogoutCommand LOGOUT_COMMAND = new LogoutCommand();
    }

    @Override
    public IFrontCommand getInstance() {
        return LogoutHolder.LOGOUT_COMMAND;
    }

    @Override
    public ExecuteResult execute(SessionContext sc) {
        ExecuteResult executeResult = new ExecuteResult();
        executeResult.setJsp("/WEB-INF/jsp/main.jsp");
        HttpSession session = sc.getSession();
//        deleteSessionAttribute(session);
        session.invalidate();
        executeResult.setCookie("", "", "", "");
        executeResult.setUpdateCookie(true);
        return executeResult;
    }


    private void deleteSessionAttribute(HttpSession session) {
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            session.removeAttribute(attributeNames.nextElement());
        }
    }


}
