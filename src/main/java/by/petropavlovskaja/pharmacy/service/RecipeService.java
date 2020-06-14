package by.petropavlovskaja.pharmacy.service;

import by.petropavlovskaja.pharmacy.dao.AccountDAO;
import by.petropavlovskaja.pharmacy.dao.MedicineDAO;
import by.petropavlovskaja.pharmacy.dao.OrderDAO;
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

public class RecipeService {
    private static Logger logger = LoggerFactory.getLogger(RecipeService.class);
    private RecipeDAO recipeDAO = RecipeDAO.getInstance();
    private AccountDAO accountDAO = AccountDAO.getInstance();
    private MedicineDAO medicineDAO = MedicineDAO.getInstance();
    private OrderDAO orderDAO = OrderDAO.getInstance();

    private RecipeService() {
    }

    private static class RecipeServiceHolder {
        public static final RecipeService RECIPE_SERVICE = new RecipeService();
    }

    public static RecipeService getInstance() {
        return RecipeServiceHolder.RECIPE_SERVICE;
    }


//    public void getRecipies(Customer customer, Map<String, Object> reqParameters){

    public Set<Recipe> getRecipes(Customer customer) {
        Set<Recipe> recipes = recipeDAO.getAllForCustomer(customer.getId());
        customer.setRecipes(recipes);
        return recipes;
    }

    public Set<Recipe> getValidRecipes(Customer customer) {
        Set<Recipe> recipes = recipeDAO.getAllValidRecipe(customer.getId());
        return recipes;
    }

    public void setNeedExtensionByID(Customer customer, Map<String, Object> reqParameters){
        int recipeId = Integer.parseInt((String) reqParameters.get("recipe_id"));
        recipeDAO.setNeedExtensionByID(recipeId);
        getRecipes(customer);
    }

    public void customerInsertRecipe(Customer customer, Map<String, Object> reqParameters){
        String medicine = (String) reqParameters.get("medicine");
        String dosage = (String) reqParameters.get("dosage");
        recipeDAO.insertRecipeCustomer(medicine, dosage, customer.getId());
        getRecipes(customer);
    }

    public void doctorInsertRecipe(String medicine, String dosage, int pharmacistId, int customerId, Date date){
        recipeDAO.insertRecipeDoctor(medicine, dosage, customerId, pharmacistId, date);
    }

    public void deleteRecipe(Customer customer, Map<String, Object> reqParameters){
        int recipeId = Integer.parseInt((String) reqParameters.get("recipe_id"));
        recipeDAO.deleteRecipe(recipeId);
        getRecipes(customer);
    }

    public void doctorDeleteRecipe(int recipeId){
        recipeDAO.deleteRecipe(recipeId);
    }

    public void validateRecipe(int accountId, Map<String, Object> reqParameters){
        int recipeId = Integer.parseInt((String) reqParameters.get("recipeId"));

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String requestDate = (String) reqParameters.get("validity");
        Date validity = null;
        try {
            validity = format.parse(requestDate);
        } catch (ParseException e) {
            logger.error("Can't parse request parameter Exp_date: " + validity + ". Error: " + e);
        }
        recipeDAO.validateRecipe(recipeId, validity, accountId);
    }

    public  Set<Recipe> getAllOrdered(){
        Set<Recipe> orderRecipe = recipeDAO.getAllOrdered();
        return orderRecipe;
    }




}
