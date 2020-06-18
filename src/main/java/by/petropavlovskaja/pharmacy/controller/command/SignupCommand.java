package by.petropavlovskaja.pharmacy.controller.command;

import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.controller.session.SessionContext;
import by.petropavlovskaja.pharmacy.model.Medicine;
import by.petropavlovskaja.pharmacy.model.account.Account;
import by.petropavlovskaja.pharmacy.model.account.AccountRole;
import by.petropavlovskaja.pharmacy.service.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class SignupCommand implements IFrontCommand {
    private static Logger logger = LoggerFactory.getLogger(SignupCommand.class);
    private CommonService service = CommonService.getInstance();

    private static class SignupHolder {
        public static final SignupCommand SIGNUP_COMMAND = new SignupCommand();
    }

    @Override
    public IFrontCommand getInstance() {
        return SignupHolder.SIGNUP_COMMAND;
    }


    @Override
    public ExecuteResult execute(SessionContext sc) {
        ExecuteResult executeResult = new ExecuteResult();
        Map<String, Object> reqParameters = sc.getRequestParameters();

        executeResult.setJsp("/WEB-INF/jsp/signup.jsp");
        if (sc.getRequestMethod().equals("POST")) {

            logger.info("Get request params for Signup: " + reqParameters.toString());
            String login = String.valueOf(reqParameters.get("login"));

            // check login/password for null, is login busy, are password & confirm equals
            String errorMessage = service.checkAccountDataBeforeCreate(reqParameters, login);
            if (errorMessage != null) {
                executeResult.setResponseAttributes("message", errorMessage);
            } else {
                Account newAccount = service.accountRegistration(reqParameters, AccountRole.CUSTOMER);
                if (newAccount.getId() != 1) {
                    String sessionId = sc.getSession().getId();
                    Set<Medicine> medicineList = service.getAllMedicine();
                    System.out.println("Registered Account is: " + newAccount.toString());
                    sc.setSessionAttributesForAccount(newAccount, login, sessionId, medicineList);
                    executeResult.setCookie(sessionId, login, String.valueOf(newAccount.getId()), AccountRole.CUSTOMER.toString());
                    executeResult.setUpdateCookie(true);
                    executeResult.setJsp("/pharmacy/customer/main");
                } else {
                    executeResult.setResponseAttributes("message", "This login already busy. Please, choose another login and try again.");
                }

            }
        }
        return executeResult;
    }

}

