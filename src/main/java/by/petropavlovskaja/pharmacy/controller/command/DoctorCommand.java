package by.petropavlovskaja.pharmacy.controller.command;

import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.controller.session.SessionContext;
import by.petropavlovskaja.pharmacy.model.Medicine;
import by.petropavlovskaja.pharmacy.model.Recipe;
import by.petropavlovskaja.pharmacy.service.CommonService;
import by.petropavlovskaja.pharmacy.service.DoctorService;
import by.petropavlovskaja.pharmacy.service.RecipeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/** Class for processing the doctor front requests commands, implements {@link IFrontCommand}
 */
public final class DoctorCommand implements IFrontCommand {
    private static Logger logger = LoggerFactory.getLogger(DoctorCommand.class);
    private static DoctorService doctorService = DoctorService.getInstance();
    private static RecipeService recipeService = RecipeService.getInstance();
    private static CommonService commonService = CommonService.getInstance();

    /** Nested class create instance of the class */
    private static class DoctorCommandHolder {
        public static final DoctorCommand DOCTOR_COMMAND = new DoctorCommand();
    }

    /**
     * The override method for get instance of the class
     * @return - class instance
     */
    @Override
    public IFrontCommand getInstance() {
        return DoctorCommandHolder.DOCTOR_COMMAND;
    }

    /**
     * The override method process doctor's GET and POST front requests
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
/*                case "history": {
                    customerService.updateCartWithDetails(customer);
                    sc.getSession().setAttribute("cart", customer.getCart());
                    sc.getSession().setAttribute("medicineInCart", customer.getMedicineInCart());
                    break;
                }
*/
                case "profile": {
                    break;
                }
            }
        }
        executeResult.setJsp(arrayUri);

        if (requestMethod.equals("POST")) {
            String command = (String) reqParameters.get("customerCommand");
            switch (command) {
                case "extendRecipe": {
                    logger.info(" Command extendRecipe is received.");
                    recipeService.validateRecipe(accountId, reqParameters);
                    executeResult.setJsp(fullUri);
                    break;
                }
                case "refuseRecipe": {
                    logger.info(" Command refuseRecipe is received.");
                    doctorService.refuseRecipe(accountId, reqParameters);
                    executeResult.setJsp(fullUri);
                    break;
                }
                case "appointRecipe": {
                    logger.info(" Command appointRecipe is received.");
                    String errorData = doctorService.createRecipe(accountId, reqParameters);

                    if (!errorData.equals("noError")) {
                        executeResult.setResponseAttributes("message", errorData);
                        executeResult.setJsp("/WEB-INF/jsp/doctor/recipe/recipe_create.jsp");
                    }
                    else {
                        executeResult.setJsp(fullUri);
                    }
                    break;
                }
                default: {
                    logger.error("Command " + command + " is not defined.");
                }
            }
        }
        return executeResult;
    }
}