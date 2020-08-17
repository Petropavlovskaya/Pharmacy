package by.petropavlovskaja.pharmacy.controller.command;

import by.petropavlovskaja.pharmacy.controller.AttributeConstant;
import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.controller.session.SessionContext;
import by.petropavlovskaja.pharmacy.model.Medicine;
import by.petropavlovskaja.pharmacy.model.account.Account;
import by.petropavlovskaja.pharmacy.service.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Class for processing the front login command, implements {@link IFrontCommand}
 */
public final class LoginCommand implements IFrontCommand {
    private static Logger logger = LoggerFactory.getLogger(LoginCommand.class);
    private static CommonService commonService = CommonService.getInstance();

    /**
     * Nested class create instance of the class
     */
    private static class LoginHolder {
        public static final LoginCommand LOGIN_COMMAND = new LoginCommand();
    }

    /**
     * The override method for get instance of the class
     *
     * @return - class instance
     */
    @Override
    public IFrontCommand getInstance() {
        return LoginHolder.LOGIN_COMMAND;
    }

    /**
     * The override method process login to the application
     *
     * @param sc - Session context {@link SessionContext}
     * @return - class instance {@link ExecuteResult}
     */
    @Override
    public ExecuteResult execute(SessionContext sc) {
        ExecuteResult executeResult = new ExecuteResult();
        Map<String, Object> reqParameters = sc.getRequestParameters();
        String requestMethod = sc.getRequestMethod();

        executeResult.setJsp("/WEB-INF/jsp/login.jsp");
        switch (requestMethod) {
            case "GET": {
                break;
            }
            case "POST": {
                String loggerMessage;
                String login = String.valueOf(reqParameters.get("login"));
                String password = String.valueOf(reqParameters.get("password"));
                loggerMessage = "Try login with Login: " + login + " and Password: " + password;
                logger.info(loggerMessage);
                if (login != null && password != null) {
                    logger.info("login and password are not NULL");
                    Account account = commonService.accountAuthentication(login, password);
                    // If AccountId = -1 then account doesn't exist or password isn't correct
                    if (account.getId() == -1) {
                        loggerMessage = "User " + login + " haven't passed authentication.";
                        logger.info(loggerMessage);
                        executeResult.setResponseAttributes(AttributeConstant.ERROR_MSG, "Login or/and password are incorrect. Please, try again.");
                    } else {
                        loggerMessage = "User " + login + " have passed authentication.";
                        logger.info(loggerMessage);
                        executeResult.setJsp("/pharmacy/" + account.getAccountRole().name().toLowerCase() + "/main");
                        String sessionId = sc.getSession().getId();
                        String lang = (String) sc.getSession().getAttribute("lang");
                        List<Medicine> medicineList = commonService.getAllMedicine();
                        sc.setSessionAttributesForAccount(account, login, sessionId, medicineList);
                        executeResult.setCookie(sessionId, login, String.valueOf(account.getId()),
                                account.getAccountRole().toString(), lang);
                        executeResult.isNeedUpdateCookie(true);
                    }
                }
                break;
            }
            default: {
                executeResult.setJsp("/pharmacy/error");
            }
        }
        return executeResult;
    }
}
