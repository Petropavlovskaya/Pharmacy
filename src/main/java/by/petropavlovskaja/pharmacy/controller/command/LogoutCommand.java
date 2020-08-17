package by.petropavlovskaja.pharmacy.controller.command;

import by.petropavlovskaja.pharmacy.controller.AttributeConstant;
import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.controller.session.SessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;

/**
 * Class for processing the front log out command, implements {@link IFrontCommand}
 */
public final class LogoutCommand implements IFrontCommand {
    private static Logger logger = LoggerFactory.getLogger(LogoutCommand.class);

    /**
     * Nested class create instance of the class
     */
    private static class LogoutHolder {
        public static final LogoutCommand LOGOUT_COMMAND = new LogoutCommand();
    }

    /**
     * The override method for get instance of the class
     *
     * @return - class instance
     */
    @Override
    public IFrontCommand getInstance() {
        return LogoutHolder.LOGOUT_COMMAND;
    }

    /**
     * The override method process log out of the application
     *
     * @param sc - Session context {@link SessionContext}
     * @return - class instance {@link ExecuteResult}
     */
    @Override
    public ExecuteResult execute(SessionContext sc) {
        ExecuteResult executeResult = new ExecuteResult();
        if (sc.getSession().getAttribute(AttributeConstant.ACCOUNT_ROLE) != null) {
            HttpSession session = sc.getSession();
            String loggerMessage = "Invalidate session for user " + session.getAttribute(AttributeConstant.ACCOUNT_LOGIN)
                    + ", id = " + session.getAttribute(AttributeConstant.ACCOUNT_ID);
            logger.info(loggerMessage);
            executeResult.setCookie("", "", "", "", "");
            executeResult.isNeedUpdateCookie(true);
            session.invalidate();
        }
        executeResult.setJsp("/WEB-INF/jsp/main.jsp");
        return executeResult;
    }
}
