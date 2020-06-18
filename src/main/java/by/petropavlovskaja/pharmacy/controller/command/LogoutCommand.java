package by.petropavlovskaja.pharmacy.controller.command;

import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.controller.session.SessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;

public class LogoutCommand implements IFrontCommand {
    private static Logger logger = LoggerFactory.getLogger(LogoutCommand.class);

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
        logger.info("Invalidate session for user " + session.getAttribute("accountLogin") + ", id = "
                + session.getAttribute("accountId"));
        session.invalidate();
        executeResult.setCookie("", "", "", "");
        executeResult.setUpdateCookie(true);
        return executeResult;
    }
}
