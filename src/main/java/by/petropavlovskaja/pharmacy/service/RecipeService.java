package by.petropavlovskaja.pharmacy.service;

import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.controller.session.SessionContext;
import by.petropavlovskaja.pharmacy.dao.RecipeDAO;
import by.petropavlovskaja.pharmacy.model.Recipe;
import by.petropavlovskaja.pharmacy.model.account.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import static by.petropavlovskaja.pharmacy.controller.AttributeConstant.*;

/**
 * Class for services of recipe instance. Uses {@link CommonService}, {@link RecipeDAO}
 */
public class RecipeService {
    private static final String RECIPE_ID = "recipeId";

    private static Logger logger = LoggerFactory.getLogger(RecipeService.class);
    private CommonService commonService = CommonService.getInstance();
    private RecipeDAO recipeDAO = RecipeDAO.getInstance();

    /**
     * Constructor without parameters
     */
    private RecipeService() {
    }

    /**
     * Nested class create instance of the class
     */
    private static class RecipeServiceHolder {
        public static final RecipeService RECIPE_SERVICE = new RecipeService();
    }

    /**
     * The method for get instance of the class
     *
     * @return - class instance
     */
    public static RecipeService getInstance() {
        return RecipeServiceHolder.RECIPE_SERVICE;
    }

    public void getRecipes(Customer customer) {
        Set<Recipe> recipes = recipeDAO.getAllCustomerRecipe(customer.getId());
        customer.setRecipes(recipes);
    }

    /**
     * The method for getting customer's recipes. Uses {@link RecipeDAO#getAllValidRecipe(int)},
     *
     * @param customer - customer instance
     * @return - recipe set
     */
    public Set<Recipe> getValidRecipes(Customer customer) {
        return recipeDAO.getAllValidRecipe(customer.getId());
    }

    /**
     * The method for setting a flag "need extension" for recipe. Uses {@link CommonService#isNumber(String)},
     * {@link RecipeDAO#setNeedExtensionByID(int)}
     *
     * @param customer      - customer instance
     * @param reqParameters - request parameters from jsp
     */
    public void setNeedExtensionByID(Customer customer, Map<String, Object> reqParameters) {
        String frontRecipeId = (String) reqParameters.get(RECIPE_ID);
        if (commonService.isNumber(frontRecipeId)) {
            int recipeId = Integer.parseInt(frontRecipeId);
            recipeDAO.setNeedExtensionByID(recipeId);
            getRecipes(customer);
        }
    }

    /**
     * The method for inserting a new record into database for recipe that need an extension.
     * Uses {@link RecipeDAO#insertRecipeCustomer(String, String, int)}, {@link SessionContext}
     *
     * @param executeResult - execute result instance
     * @param customer      - customer instance
     * @param reqParameters - request parameters from jsp
     * @param sc            - SessionContext instance
     */
    public void customerInsertRecipe(ExecuteResult executeResult, Customer customer, Map<String, Object> reqParameters,
                                     SessionContext sc) {
        String medicine = (String) reqParameters.get("medicine");
        String dosage = (String) reqParameters.get("dosage");
        boolean recipePresent = checkRecipeAlreadyPresent(customer, medicine, dosage);
        if (recipePresent) {
            executeResult.setResponseAttributes(ERROR_MSG, "You already have recipe for " + medicine + " or it was send to a Doctor earlier.");
            executeResult.setJsp("/WEB-INF/jsp/customer/cabinet/cabinet_recipe.jsp");
        } else {
            recipeDAO.insertRecipeCustomer(medicine, dosage, customer.getId());
            getRecipes(customer);
            sc.getSession().setAttribute(SUCCESS_MSG, "The request for recipe has sent to Doctor successfully.");
            sc.getSession().setAttribute(SUCCESS_MSG_CHECK, "yes");
        }
    }

    /**
     * The method for checking is a recipe already has flag "need extension".
     *
     * @param customer - customer instance
     * @param medicine - medicine name
     * @param dosage   - medicine dosage
     * @return - true if recipe is already in extension
     */
    public boolean checkRecipeAlreadyPresent(Customer customer, String medicine, String dosage) {
        Set<Recipe> recipes = customer.getRecipes();
        boolean recipePresent = false;
        for (Recipe recipe : recipes) {
            if (recipe.getMedicine().equals(medicine) && (recipe.getDosage().equals(dosage)) &&
                    (recipe.getIdMedicineInOrder() <= 0 || recipe.getValidity().before(new Date()))) {
                recipePresent = true;
                break;
            }
        }
        return recipePresent;
    }

    /**
     * The method for inserting a recipe into database.
     * Uses {@link RecipeDAO#insertRecipeDoctor(String, String, int, int, Date)}
     *
     * @param medicine     - medicine name
     * @param dosage       - medicine dosage
     * @param customerId   - customer ID
     * @param pharmacistId - pharmacist ID
     * @param date         - recipe validity date
     */
    public void doctorInsertRecipe(String medicine, String dosage, int pharmacistId, int customerId, Date date) {
        recipeDAO.insertRecipeDoctor(medicine, dosage, customerId, pharmacistId, date);
    }

    /**
     * The method for deleting the recipe from database.
     * Uses {@link CommonService#isNumber(String)}, {@link RecipeDAO#deleteRecipe(int)}, {@link SessionContext}
     *
     * @param customer      - customer instance
     * @param reqParameters - request parameters from jsp
     * @param sc            - SessionContext instance
     */
    public void deleteRecipe(Customer customer, Map<String, Object> reqParameters, SessionContext sc) {
        String frontRecipeId = (String) reqParameters.get(RECIPE_ID);
        if (commonService.isNumber(frontRecipeId)) {
            int recipeId = Integer.parseInt(frontRecipeId);
            recipeDAO.deleteRecipe(recipeId);
            getRecipes(customer);
            sc.getSession().setAttribute(SUCCESS_MSG, "The recipe was delete successfully.");
            sc.getSession().setAttribute(SUCCESS_MSG_CHECK, "yes");
        }
    }

    /**
     * The method for refusing an extension by recipe. Uses {@link RecipeDAO#refuseRecipe(int, int)}
     *
     * @param accountId - account ID
     * @param recipeId  - recipe ID
     */
    public void doctorRefuseRecipe(int accountId, int recipeId) {
        recipeDAO.refuseRecipe(accountId, recipeId);
    }

    /**
     * The method for extension the recipe. Uses {@link CommonService#isNumber(String)}, {@link RecipeDAO#validateRecipe(int, Date, int)},
     * {@link SessionContext}
     *
     * @param accountId     - account ID
     * @param reqParameters - request parameters from jsp
     * @param executeResult - execute result instance
     * @param sc            - SessionContext instance
     */
    public void validateRecipe(int accountId, Map<String, Object> reqParameters, ExecuteResult executeResult,
                               SessionContext sc) {
        String frontRecipeId = (String) reqParameters.get(RECIPE_ID);
        if (commonService.isNumber(frontRecipeId)) {
            int recipeId = Integer.parseInt(frontRecipeId);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String requestDate = (String) reqParameters.get("validity");
            Date validity;
            try {
                validity = format.parse(requestDate);
                boolean validDate = DoctorService.getInstance().isDateValid(validity);
                if (validDate) {
                    recipeDAO.validateRecipe(recipeId, validity, accountId);
                    sc.getSession().setAttribute(SUCCESS_MSG, "The recipe has extended successfully.");
                    sc.getSession().setAttribute(SUCCESS_MSG_CHECK, "yes");
                } else {
                    executeResult.setJsp("/WEB-INF/jsp/doctor/recipe/recipe_ordered.jsp");
                    executeResult.setResponseAttributes(ERROR_MSG, "The date is invalid. Please, enter the correct data and rty again.");
                }
            } catch (ParseException e) {
                executeResult.setJsp("/WEB-INF/jsp/doctor/recipe/recipe_ordered.jsp");
                executeResult.setResponseAttributes(ERROR_MSG, "The date is invalid. Please, enter the correct data and rty again.");
                logger.error("Can't parse request parameter ExpDate. Error: ", e);
            }
        }
    }

    /**
     * The method for getting ordered recipes. Uses {@link RecipeDAO#getAllOrdered()}
     *
     * @return - recipe set
     */
    public Set<Recipe> getAllOrdered() {
        return recipeDAO.getAllOrdered();
    }
}
