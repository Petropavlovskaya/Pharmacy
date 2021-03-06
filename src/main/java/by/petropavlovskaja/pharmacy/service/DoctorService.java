package by.petropavlovskaja.pharmacy.service;

import by.petropavlovskaja.pharmacy.controller.session.SessionContext;
import by.petropavlovskaja.pharmacy.dao.AccountDAO;
import by.petropavlovskaja.pharmacy.dao.MedicineDAO;
import by.petropavlovskaja.pharmacy.dao.RecipeDAO;
import by.petropavlovskaja.pharmacy.model.Medicine;
import by.petropavlovskaja.pharmacy.model.Recipe;
import by.petropavlovskaja.pharmacy.model.account.Account;
import by.petropavlovskaja.pharmacy.model.account.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static by.petropavlovskaja.pharmacy.controller.AttributeConstant.*;

/**
 * Class for services of doctor role. Uses {@link RecipeService}, {@link CommonService}, {@link AccountDAO},
 * {@link MedicineDAO}, {@link RecipeDAO}
 */
public class DoctorService {
    private static final String NO_ERROR = "noError";
    private static final String ERROR = "error";
    private static final String RECIPE_ID = "recipeId";
    private static final String MEDICINE_NAME = "medicineName";
    private static final String MEDICINE_DOSAGE = "medicineDosage";
    private static final String CUSTOMER_ID = "customerId";
    private static Logger logger = LoggerFactory.getLogger(DoctorService.class);
    private RecipeService recipeService = RecipeService.getInstance();
    private CommonService commonService = CommonService.getInstance();
    private AccountDAO accountDAO = AccountDAO.getInstance();
    private MedicineDAO medicineDAO = MedicineDAO.getInstance();
    private RecipeDAO recipeDAO = RecipeDAO.getInstance();

    /**
     * Constructor without parameters
     */
    private DoctorService() {
    }

    /**
     * Nested class create instance of the class
     */
    private static class DoctorServiceHolder {
        public static final DoctorService DOCTOR_SERVICE = new DoctorService();
    }

    /**
     * The method for get instance of the class
     *
     * @return - class instance
     */
    public static DoctorService getInstance() {
        return DoctorServiceHolder.DOCTOR_SERVICE;
    }

    /**
     * The method for getting a set of ordered recipes. Uses {@link RecipeService#getAllOrdered()}
     *
     * @return -set of ordered recipes
     */
    public Set<Recipe> getOrderedRecipe() {
        return recipeService.getAllOrdered();
    }

    /**
     * The method for getting a set of ordered recipes. Uses {@link CommonService#isNumber(String)},
     * {@link RecipeService#getAllOrdered()}, {@link SessionContext}
     *
     * @param accountId     - doctor account ID
     * @param reqParameters - request parameters from jsp
     * @param sc            - SessionContext instance
     */
    public void refuseRecipe(int accountId, Map<String, Object> reqParameters, SessionContext sc) {
        String frontRecipeId = (String) reqParameters.get(RECIPE_ID);
        if (commonService.isNumber(frontRecipeId)) {
            int recipeId = Integer.parseInt(frontRecipeId);
            recipeService.doctorRefuseRecipe(accountId, recipeId);
            sc.getSession().setAttribute(SUCCESS_MSG, "The recipe has refused successfully.");
            sc.getSession().setAttribute(SUCCESS_MSG_CHECK, "yes");
        }
    }

    /**
     * The method for getting an active customers. Uses {@link AccountDAO#getActiveCustomers()}
     *
     * @return - map of customers
     */
    public Map<Integer, String> getActiveCustomers() {
        Set<Account> accountSet = accountDAO.getActiveCustomers();
        Map<Integer, String> customerMap = new TreeMap<>();
        for (Account account : accountSet) {
            if (account.isActive()) {
                customerMap.put(account.getId(), (account.getSurname() + " " + account.getName() + " " + account.getPatronymic() + " " +
                        account.getPhoneNumber()));
            }
        }
        return customerMap;
    }

    /**
     * The method for getting an available medicines. Uses {@link MedicineDAO#getAllForDoctor()}
     *
     * @return - set of medicines
     */
    public Set<Medicine> getAvailableMedicine() {
        return medicineDAO.getAllForDoctor();
    }

    /**
     * The method for creating recipe for the customer. Uses {@link AccountDAO#findCustomerById(int)},
     * {@link MedicineDAO#isMedicine(String, String)}, {@link CommonService#isStringDate(String)},
     * {@link RecipeService#doctorInsertRecipe(String, String, int, int, Date)}, {@link SessionContext}
     *
     * @param accountId     - doctor account ID
     * @param reqParameters - request parameters from jsp
     * @param sc            - SessionContext instance
     * @return - string with error details or string "noError"
     */
    public String createRecipe(int accountId, Map<String, Object> reqParameters, SessionContext sc) {
        String errorString;

        //  check customer exist in database
        Map<String, String> customerExistResult = beforeCreateRecipeCheckCustomer(reqParameters);
        errorString = customerExistResult.get(ERROR);
        if (errorString.equals(NO_ERROR)) {
            //  check medicine exist in database
            Map<String, String> medicineExistResult = beforeCreateRecipeCheckMedicine(reqParameters);
            errorString = customerExistResult.get(ERROR);
            if (errorString.equals(NO_ERROR)) {
                //  check date is valid
                String requestDate = (String) reqParameters.get("expDate");
                Date expDate = convertStringToDate(requestDate);
                if (expDate.equals(new Date())) {
                    errorString = "Next date for recipe is invalid:" + requestDate + " (min 2 days, max 2 month). Please enter the correct info and try again.";
                } else {
                    boolean validDate = isDateValid(expDate);
                    if (validDate) {
                        String medicineName = medicineExistResult.get(MEDICINE_NAME);
                        String medicineDosage = medicineExistResult.get(MEDICINE_DOSAGE);
                        int customerId = Integer.parseInt(customerExistResult.get(CUSTOMER_ID));
                        int recipeId = checkRecipeOrdered(medicineName, medicineDosage, customerId);
                        if (recipeId != -1) {
                            // if customer has invalidate recipe for current medicine - set recipe valid
                            recipeDAO.validateRecipe(recipeId, expDate, accountId);
                        } else {
                            // if customer hasn't invalidate recipe for current medicine - create new valid recipe
                            recipeService.doctorInsertRecipe(medicineName, medicineDosage, accountId, customerId, expDate);
                        }
                        sc.getSession().setAttribute(SUCCESS_MSG, "The recipe has added successfully.");
                        sc.getSession().setAttribute(SUCCESS_MSG_CHECK, "yes");
                    } else {
                        return "Next date for recipe is invalid:" + requestDate + ". Please enter the correct info and try again.";
                    }
                }
            }
        }
        return errorString;
    }

    /**
     * The method for checking customer for exist in database. Method uses {@link AccountDAO#findCustomerById(int)}
     *
     * @param reqParameters - request parameters from jsp
     * @return map of "error" and "customerId" keys.
     * The key "error" contains "noError" string if method passed successfully or error details string.
     * The key "customerId" contains "-1" if the customer doesn't exist or customer ID.
     */
    public Map<String, String> beforeCreateRecipeCheckCustomer(Map<String, Object> reqParameters) {
        Map<String, String> result = new HashMap<>();
        result.put(ERROR, NO_ERROR);
        result.put(CUSTOMER_ID, "-1");
        String customer = (String) reqParameters.get("customer");
        String[] request = customer.split(";");
        String error;
        int customerId;
        if (request.length != 2) {
            error = "Customer " + customer + " is not present in database. Please enter the correct account and try again.";
            result.replace(ERROR, NO_ERROR, error);
        } else {
            String frontCustomerId = request[1].trim();
            if (commonService.isNumber(frontCustomerId)) {
                customerId = Integer.parseInt(frontCustomerId);
                result.replace(CUSTOMER_ID, "-1", frontCustomerId);
                Customer requestCustomer = accountDAO.findCustomerById(customerId);
                if (requestCustomer.getId() == -1) {
                    error = "Customer " + customer + " is not present in database. Please enter the correct account and try again.";
                    result.replace(ERROR, NO_ERROR, error);
                } else {
                    String frontFio = request[0].trim();
                    boolean surnameValid = frontFio.contains(requestCustomer.getSurname());
                    boolean nameValid = frontFio.contains(requestCustomer.getName());
                    boolean patronymicValid = frontFio.contains(requestCustomer.getPatronymic());
                    if (!(surnameValid && nameValid && patronymicValid)) {
                        error = "Customer ID doesn't match customer personal info.";
                        result.replace(ERROR, NO_ERROR, error);
                    }
                }
            } else {
                error = "Customer ID = " + frontCustomerId + " is invalid data. Please enter the correct account and try again.";
                result.replace(ERROR, NO_ERROR, error);
            }
        }
        return result;
    }

    /**
     * The method for checking customer for exist in database. Method uses {@link AccountDAO#findCustomerById(int)}
     *
     * @param reqParameters - request parameters from jsp
     * @return map of "error", "medicineName" and "medicineDosage" keys.
     * The key "error" contains "noError" string if method passed successfully or error details string.
     * The key "medicineName" contains NULL if the medicine name doesn't exist.
     * The key "medicineDosage" contains NULL if the medicine dosage doesn't exist.
     */
    public Map<String, String> beforeCreateRecipeCheckMedicine(Map<String, Object> reqParameters) {
        Map<String, String> result = new HashMap<>();
        result.put(ERROR, NO_ERROR);
        result.put(MEDICINE_NAME, null);
        result.put(MEDICINE_DOSAGE, null);
        String medicine = (String) reqParameters.get("medicine");
        String[] request = medicine.split(";");
        String medicineName;
        String medicineDosage;
        String error;
        if (request.length != 2) {
            error = "Medicine name " + medicine + " is invalid. Please enter the correct info and try again.";
            result.replace(ERROR, NO_ERROR, error);
        } else {
            medicineName = request[0].trim();
            medicineDosage = request[1].trim();
            boolean validMedicine = medicineDAO.isMedicine(medicineName, medicineDosage);
            if (!validMedicine) {
                error = "Medicine name " + medicine + " is invalid. Please enter the correct info and try again.";
                result.replace(ERROR, NO_ERROR, error);
            } else {
                result.replace(MEDICINE_NAME, null, medicineName);
                result.replace(MEDICINE_DOSAGE, null, medicineDosage);
            }
        }
        return result;
    }

    public Date convertStringToDate(String date) {
        Date expDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (commonService.isStringDate(date)) {
            try {
                expDate = format.parse(date);
            } catch (ParseException e) {
                logger.trace("Parse exception in a method convertStringToDate. ", e);
                return expDate;
            }
        }
        return expDate;
    }

    /**
     * The method for checking is the creating recipe already ordered. Uses {@link RecipeService#getAllOrdered()}
     *
     * @param medicineName   - medicine name
     * @param medicineDosage - medicine dosage
     * @param customerId     - customer ID
     * @return - recipe ID if it is already exist or -1
     */
    private int checkRecipeOrdered(String medicineName, String medicineDosage, int customerId) {
        int recipeId = -1;
        Set<Recipe> ordered = recipeService.getAllOrdered();
        for (Recipe recipe : ordered) {
            if (recipe.getMedicine().equals(medicineName) && recipe.getDosage().equals(medicineDosage)
                    && recipe.getFkCustomer() == customerId) {
                return recipe.getId();
            }
        }
        return recipeId;
    }

    /**
     * The method for checking is the recipe date from request valid. The minimum date mast be Today+2,
     * the maximum date mast by Today+60. Uses {@link CommonService#getFutureDate(int)}
     *
     * @param requestDate - request recipe date from jsp
     * @return - true if the recipe date is correct
     */
    public boolean isDateValid(Date requestDate) {
        Date minValidDate = commonService.getFutureDate(2);
        Date maxValidDate = commonService.getFutureDate(60);
        return requestDate.compareTo(minValidDate) > 0 && requestDate.compareTo(maxValidDate) < 0;
    }
}
