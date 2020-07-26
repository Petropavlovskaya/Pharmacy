package by.petropavlovskaja.pharmacy.controller.command;

import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
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
        if (sc.getSession().getAttribute("successTextSet") != null) {
            if (sc.getSession().getAttribute("successTextSet").equals("yes")) {
                sc.getSession().setAttribute("successTextSet", "no");
            } else {
                sc.getSession().removeAttribute("successMessage");
            }
        }

        if (requestMethod.equals("GET")) {
            String lastUri = arrayUri[arrayUri.length - 1];
            switch (lastUri) {
                case "ordered": {
                    Set<Recipe> recipes = doctorService.getOrderedRecipe();
                    sc.getSession().setAttribute("recipe", recipes);
                    String minDate = commonService.getStringDate(2);
                    String maxDate = commonService.getStringDate(60);
                    sc.getSession().setAttribute("minDate", minDate);
                    sc.getSession().setAttribute("maxDate", maxDate);
                    break;
                }
                case "create": {
                    Map<Integer, String> activeCustomers = doctorService.getActiveCustomers();
                    Set<Medicine> availableMedicine = doctorService.getAvailableMedicine();
                    String minDate = commonService.getStringDate(2);
                    String maxDate = commonService.getStringDate(60);
                    sc.getSession().setAttribute("minDate", minDate);
                    sc.getSession().setAttribute("maxDate", maxDate);
                    sc.getSession().setAttribute("activeCustomers", activeCustomers);
                    sc.getSession().setAttribute("availableMedicine", availableMedicine);
                    break;
                }
                case "profile": {
                    executeResult.setResponseAttributes("account", doctor);
                    break;
                }
            }
        }
        executeResult.setJsp(arrayUri);

        if (requestMethod.equals("POST")) {
            String command = (String) reqParameters.get("frontCommand");
            switch (command) {
                case "extendRecipe": {
                    logger.info(" Command extendRecipe is received.");
                    boolean result = recipeService.validateRecipe(accountId, reqParameters, executeResult);
                    if (result) {
                        sc.getSession().setAttribute("successMessage", "The recipe has extended successfully.");
                        sc.getSession().setAttribute("successTextSet", "yes");
                        executeResult.setJsp(fullUri);
                    } else {
                        executeResult.setJsp("/WEB-INF/jsp/doctor/recipe/recipe_ordered.jsp");
                    }
                    break;
                }
                case "refuseRecipe": {
                    logger.info(" Command refuseRecipe is received.");
                    doctorService.refuseRecipe(accountId, reqParameters);
                    sc.getSession().setAttribute("successMessage", "The recipe has refused successfully.");
                    sc.getSession().setAttribute("successTextSet", "yes");
                    executeResult.setJsp(fullUri);
                    break;
                }
                case "createRecipe": {
                    logger.info(" Command createRecipe is received.");
                    String errorMessage = doctorService.createRecipe(accountId, reqParameters);

                    if (!errorMessage.equals("noError")) {
//                        executeResult.setResponseAttributes("msgErrorText", "The changes have not saved. " + errorMessage);
                        executeResult.setResponseAttributes("errorMessage", errorMessage);
                        executeResult.setJsp("/WEB-INF/jsp/doctor/recipe/recipe_create.jsp");
                    } else {
                        sc.getSession().setAttribute("successMessage", "The recipe has added successfully.");
                        sc.getSession().setAttribute("successTextSet", "yes");
                        executeResult.setJsp(fullUri);
                    }
                    break;
                }
                case "changeAccountData": {
                    boolean successfulUpdate = commonService.changeAccountData(doctor, reqParameters, accountId);
                    if (!successfulUpdate) {
                        executeResult.setResponseAttributes("errorMessage", "Something went wrong, please contact with the application administrator.");
                    }
                    executeResult.setJsp(fullUri);
                    break;
                }
                case "changeAccountPassword": {
                    String login = (String) sc.getSession().getAttribute("accountLogin");
                    boolean result = commonService.changePassword(reqParameters, executeResult, login, sc);
                    if (result) {
                        executeResult.setJsp(fullUri);
                    } else {
                        executeResult.setJsp("/WEB-INF/jsp/doctor/cabinet/cabinet_profile.jsp");
                    }
                    break;
                }
                default: {
                    logger.error("Command " + command + " is not defined.");
                    executeResult.setJsp("/WEB-INF/jsp/404.jsp");
                    executeResult.setResponseAttributes("errorMessage", "Command is not defined.");
                }
            }
        }
        return executeResult;
    }
}