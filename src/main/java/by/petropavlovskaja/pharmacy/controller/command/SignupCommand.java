package by.petropavlovskaja.pharmacy.controller.command;

import by.petropavlovskaja.pharmacy.controller.AttributeConstant;
import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.controller.session.SessionContext;
import by.petropavlovskaja.pharmacy.model.Medicine;
import by.petropavlovskaja.pharmacy.model.account.Account;
import by.petropavlovskaja.pharmacy.model.account.AccountRole;
import by.petropavlovskaja.pharmacy.service.CommonService;

import java.util.List;
import java.util.Map;

/**
 * Class for processing the front sign up command, implements {@link IFrontCommand}
 */
public final class SignupCommand implements IFrontCommand {
    private CommonService service = CommonService.getInstance();

    /**
     * Nested class create instance of the class
     */
    private static class SignupHolder {
        public static final SignupCommand SIGNUP_COMMAND = new SignupCommand();
    }

    /**
     * The override method for get instance of the class
     *
     * @return - class instance
     */
    @Override
    public IFrontCommand getInstance() {
        return SignupHolder.SIGNUP_COMMAND;
    }

    /**
     * The override method process sign up in the application
     *
     * @param sc - Session context {@link SessionContext}
     * @return - class instance {@link ExecuteResult}
     */
    @Override
    public ExecuteResult execute(SessionContext sc) {
        ExecuteResult executeResult = new ExecuteResult();
        Map<String, Object> reqParameters = sc.getRequestParameters();

        executeResult.setJsp("/WEB-INF/jsp/signup.jsp");
        if (sc.getRequestMethod().equals("POST")) {

            String login = String.valueOf(reqParameters.get("login"));

            String errorMessage = service.checkAccountDataBeforeCreate(reqParameters, login);
            if (errorMessage != null) {
                executeResult.setResponseAttributes(AttributeConstant.ERROR_MSG, errorMessage);
                executeResult.setResponseAttributes("surname", reqParameters.get("accountSurname"));
                executeResult.setResponseAttributes("name", reqParameters.get("accountName"));
                executeResult.setResponseAttributes("login", login);
                if (!reqParameters.get("accountPatronymic").toString().isEmpty()) {
                    executeResult.setResponseAttributes("patronymic", reqParameters.get("accountPatronymic"));
                }
                if (!reqParameters.get("accountPhone").toString().isEmpty()) {
                    executeResult.setResponseAttributes("phone", reqParameters.get("accountPhone"));
                }
            } else {
                Account newAccount = service.accountRegistration(reqParameters, AccountRole.CUSTOMER);
                if (newAccount.getId() != 1) {
                    String sessionId = sc.getSession().getId();
                    String lang = (String) sc.getSession().getAttribute("lang");
                    List<Medicine> medicineList = service.getAllMedicine();
                    sc.setSessionAttributesForAccount(newAccount, login, sessionId, medicineList);
                    executeResult.setCookie(sessionId, login, String.valueOf(newAccount.getId()),
                            AccountRole.CUSTOMER.toString(), lang);
                    executeResult.isNeedUpdateCookie(true);
                    executeResult.setJsp("/pharmacy/customer/main");
                }
            }
        }
        return executeResult;
    }

}

