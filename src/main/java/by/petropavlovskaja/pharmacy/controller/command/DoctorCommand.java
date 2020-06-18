package by.petropavlovskaja.pharmacy.controller.command;

import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.controller.session.SessionContext;
import by.petropavlovskaja.pharmacy.model.Medicine;
import by.petropavlovskaja.pharmacy.model.Recipe;
import by.petropavlovskaja.pharmacy.service.DoctorService;
import by.petropavlovskaja.pharmacy.service.RecipeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class DoctorCommand implements IFrontCommand {
    private static Logger logger = LoggerFactory.getLogger(DoctorCommand.class);
    private static DoctorService doctorService = DoctorService.getInstance();
    private static RecipeService recipeService = RecipeService.getInstance();

    private static class DoctorCommandHolder {
        public static final DoctorCommand DOCTOR_COMMAND = new DoctorCommand();
    }

    @Override
    public IFrontCommand getInstance() {
        return DoctorCommandHolder.DOCTOR_COMMAND;
    }

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
                    break;
                }
                case "create": {
                    Map<Integer, String> activeCustomers = doctorService.getActiveCustomers();
                    Set<Medicine> availableMedicine = doctorService.getAvailableMedicine();
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
                    break;
                }
                case "deleteRecipe": {
                    logger.info(" Command deleteRecipe is received.");
                    doctorService.deleteRecipe(reqParameters);
                    break;
                }
                case "appointRecipe": {
                    doctorService.createRecipe(accountId, reqParameters);
                    logger.info(" Command appointRecipe is received.");
                    break;
                }
                default: {
                    logger.error("Command " + command + " is not defined.");
                }
            }
            executeResult.setJsp(fullUri);
        }
        return executeResult;
    }
}