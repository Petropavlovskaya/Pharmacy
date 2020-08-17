package by.petropavlovskaja.pharmacy.controller.command;

import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.controller.AttributeConstant;
import by.petropavlovskaja.pharmacy.controller.session.SessionContext;
import by.petropavlovskaja.pharmacy.model.Medicine;
import by.petropavlovskaja.pharmacy.model.Recipe;
import by.petropavlovskaja.pharmacy.model.account.Account;
import by.petropavlovskaja.pharmacy.service.CommonService;
import by.petropavlovskaja.pharmacy.service.DoctorService;
import by.petropavlovskaja.pharmacy.service.RecipeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

/**
 * Class for processing the doctor front requests commands, implements {@link IFrontCommand}
 */
public final class DoctorCommand implements IFrontCommand {
    private static Logger logger = LoggerFactory.getLogger(DoctorCommand.class);
    private static DoctorService doctorService = DoctorService.getInstance();
    private static RecipeService recipeService = RecipeService.getInstance();
    private static CommonService commonService = CommonService.getInstance();

    /**
     * Nested class create instance of the class
     */
    private static class DoctorCommandHolder {
        public static final DoctorCommand DOCTOR_COMMAND = new DoctorCommand();
    }

    /**
     * The override method for get instance of the class
     *
     * @return - class instance
     */
    @Override
    public IFrontCommand getInstance() {
        return DoctorCommandHolder.DOCTOR_COMMAND;
    }

    /**
     * The override method process doctor's GET and POST front requests
     *
     * @param sc - Session context {@link SessionContext}
     * @return - class instance {@link ExecuteResult}
     */
    @Override
    public ExecuteResult execute(SessionContext sc) {
        ExecuteResult executeResult = new ExecuteResult();
        Map<String, Object> reqParameters = sc.getRequestParameters();
        String fullUri = (String) sc.getSession().getAttribute("fullUri");
        String[] arrayUri = fullUri.substring(1).split("/");

        String requestMethod = sc.getRequestMethod();
        executeResult.setJsp(arrayUri);
        int accountId = Integer.parseInt(String.valueOf(sc.getSession().getAttribute("accountId")));
        Account doctor = commonService.getAccount(accountId);

        commonService.checkSuccessMessageSet(sc);

        executeResult.setJsp(arrayUri);
        if (requestMethod.equals("GET")) {
            String lastUri = arrayUri[arrayUri.length - 1];
            switch (lastUri) {
                case "ordered": {
                    Set<Recipe> recipes = doctorService.getOrderedRecipe();
                    sc.getSession().setAttribute(AttributeConstant.RECIPE, recipes);
                    String minDate = commonService.getStringDate(2);
                    String maxDate = commonService.getStringDate(60);
                    sc.getSession().setAttribute(AttributeConstant.MIN_DATE, minDate);
                    sc.getSession().setAttribute(AttributeConstant.MAX_DATE, maxDate);
                    break;
                }
                case "create": {
                    Map<Integer, String> activeCustomers = doctorService.getActiveCustomers();
                    Set<Medicine> availableMedicine = doctorService.getAvailableMedicine();
                    String minDate = commonService.getStringDate(2);
                    String maxDate = commonService.getStringDate(60);
                    sc.getSession().setAttribute(AttributeConstant.MIN_DATE, minDate);
                    sc.getSession().setAttribute(AttributeConstant.MAX_DATE, maxDate);
                    sc.getSession().setAttribute(AttributeConstant.ACTIVE_CUSTOMERS, activeCustomers);
                    sc.getSession().setAttribute(AttributeConstant.AVAILABLE_MEDICINE, availableMedicine);
                    break;
                }
                case "profile": {
                    executeResult.setResponseAttributes(AttributeConstant.ACCOUNT, doctor);
                    break;
                }
                case "main": {
                    break;
                }
                default: {
                    String error = "Uri " + lastUri + " was not define.";
                    logger.error(error);
                    executeResult.setJsp("/WEB-INF/jsp/404.jsp");
                    executeResult.setResponseAttributes(AttributeConstant.ERROR_MSG, error);
                }
            }
        }

        if (requestMethod.equals("POST")) {
            executeResult.setJsp(fullUri);
            String command = (String) reqParameters.get("frontCommand");
            switch (command) {
                case "extendRecipe": {
                    logger.info(" Command extendRecipe is received.");
                    recipeService.validateRecipe(accountId, reqParameters, executeResult, sc);
                    break;
                }
                case "refuseRecipe": {
                    logger.info(" Command refuseRecipe is received.");
                    doctorService.refuseRecipe(accountId, reqParameters, sc);
                    break;
                }
                case "createRecipe": {
                    logger.info(" Command createRecipe is received.");
                    String errorMessage = doctorService.createRecipe(accountId, reqParameters, sc);
                    if (!errorMessage.equals("noError")) {
                        executeResult.setResponseAttributes(AttributeConstant.ERROR_MSG, errorMessage);
                        executeResult.setJsp("/WEB-INF/jsp/doctor/recipe/recipe_create.jsp");
                    }
                    break;
                }
                case "changeAccountData": {
                    boolean result = commonService.changeAccountData(doctor, reqParameters, accountId, sc);
                    if (!result) {
                        executeResult.setResponseAttributes(AttributeConstant.ERROR_MSG, "Something went wrong, please contact with the application administrator.");
                        executeResult.setJsp("/WEB-INF/jsp/doctor/cabinet/cabinet_profile.jsp");
                    }
                    break;
                }
                case "changeAccountPassword": {
                    String login = (String) sc.getSession().getAttribute(AttributeConstant.ACCOUNT_LOGIN);
                    boolean result = commonService.changePassword(reqParameters, executeResult, login, sc);
                    if (!result) {
                        executeResult.setJsp("/WEB-INF/jsp/doctor/cabinet/cabinet_profile.jsp");
                    }
                    break;
                }
                default: {
                    String error = "Command " + command + " is not defined.";
                    logger.error(error);
                    executeResult.setJsp("/WEB-INF/jsp/404.jsp");
                    executeResult.setResponseAttributes(AttributeConstant.ERROR_MSG, "Command is not defined.");
                }
            }
        }
        return executeResult;
    }

}