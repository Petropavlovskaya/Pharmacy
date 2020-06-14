package by.petropavlovskaja.pharmacy.controller.command;

import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.controller.session.SessionContext;
import by.petropavlovskaja.pharmacy.model.Medicine;
import by.petropavlovskaja.pharmacy.model.account.Account;
import by.petropavlovskaja.pharmacy.model.account.AccountRole;
import by.petropavlovskaja.pharmacy.service.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LoginCommand implements IFrontCommand {
    private static Logger logger = LoggerFactory.getLogger(LoginCommand.class);
    private static CommonService commonService = CommonService.getInstance();
    private Cookie cookie;

    private static class LoginHolder {
        public static final LoginCommand LOGIN_COMMAND = new LoginCommand();
    }

    @Override
    public IFrontCommand getInstance() {
        return LoginHolder.LOGIN_COMMAND;
    }

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
                logger.info("Get request params for LoginCommand: " + reqParameters.toString());

                String login = String.valueOf(reqParameters.get("login"));
                String password = String.valueOf(reqParameters.get("password"));
                logger.info("Try login with Login: " + login + " and Password: " + password);
                if (login != null && password != null) {
                    logger.info("login and password are not NULL");
                    Account account = commonService.accountAuthentication(login, password);
                    // If AccountId = -1 then account doesn't exist or password isn't correct
                    if (account.getId() == -1) {
                        logger.info("User " + login + " haven't passed authentication.");
                        executeResult.setResponseAttributes("message", "Login or/and password are incorrect. Please, try again.");
                    } else {
                        logger.info("User " + login + " have passed authentication.");
                        System.out.println("Class LoginCommand. Account role is: " + account.getAccountRole());
                        executeResult.setJsp("/pharmacy/" + account.getAccountRole().name().toLowerCase() + "/main");
                        String sessionId = sc.getSession().getId();
                        Set<Medicine> medicineList = commonService.getAllMedicine();
                        sc.setSessionAttributesForAccount(account, login, sessionId, medicineList);
                        executeResult.setCookie(sessionId, login, String.valueOf(account.getId()), account.getAccountRole().toString());
                        executeResult.setUpdateCookie(true);
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
