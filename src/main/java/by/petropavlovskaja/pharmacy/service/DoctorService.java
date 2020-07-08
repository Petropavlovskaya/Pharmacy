package by.petropavlovskaja.pharmacy.service;

import by.petropavlovskaja.pharmacy.dao.AccountDAO;
import by.petropavlovskaja.pharmacy.dao.MedicineDAO;
import by.petropavlovskaja.pharmacy.dao.OrderDAO;
import by.petropavlovskaja.pharmacy.model.Medicine;
import by.petropavlovskaja.pharmacy.model.Recipe;
import by.petropavlovskaja.pharmacy.model.account.Account;
import by.petropavlovskaja.pharmacy.model.account.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
/** Class for services of doctor role. Uses {@link RecipeService}, {@link CommonService}, {@link AccountDAO},
 * {@link MedicineDAO}
 */
public class DoctorService {
    private static Logger logger = LoggerFactory.getLogger(DoctorService.class);
    private RecipeService recipeService = RecipeService.getInstance();
    private CommonService commonService = CommonService.getInstance();
    private AccountDAO accountDAO = AccountDAO.getInstance();
    private MedicineDAO medicineDAO = MedicineDAO.getInstance();

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
     * {@link RecipeService#getAllOrdered()}
     *
     * @param accountId - doctor account ID
     * @param reqParameters - request parameters from jsp
     */
    public void refuseRecipe(int accountId, Map<String, Object> reqParameters) {
        String frontRwcipeId = (String) reqParameters.get("recipeId");
        if (commonService.isNumber(frontRwcipeId)) {
            int recipeId = Integer.parseInt(frontRwcipeId);
            recipeService.doctorRefuseRecipe(accountId, recipeId);
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
            customerMap.put(account.getId(), (account.getSurname() + " " + account.getName() + " " + account.getPatronymic() + " " +
                    account.getPhoneNumber()));
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
     * {@link RecipeService#doctorInsertRecipe(String, String, int, int, Date)}
     *
     * @param accountId - doctor account ID
     * @param reqParameters - request parameters from jsp
     * @return - string with error or string "noError"
     */
    public String createRecipe(int accountId, Map<String, Object> reqParameters) {
        String errorString = "noError";
        String[] request;

        String customer = (String) reqParameters.get("customer");
        request = customer.split(";");
        int customerId = -1;
        if (request.length != 2) {
            return "Cusromer " + customer + " is not available in database. Please enter the correct account and try again.";
        } else {
            customerId = Integer.parseInt(request[1].trim());
            Customer requestCustomer = accountDAO.findCustomerById(customerId);
            if (requestCustomer.getId() == -1) {
                return "Cusromer " + customer + " is not available in database. Please enter the correct account and try again.";
            }
        }

        String medicine = (String) reqParameters.get("medicine");
        request = medicine.split(";");
        String medicineName = "";
        String medicineDosage = "";
        if (request.length != 2) {
            return "Medicine name " + medicine + " is invalid. Please enter the correct info and try again.";
        } else {
            medicineName = request[0].trim();
            medicineDosage = request[1].trim();
            boolean validMedicine = medicineDAO.isMedicine(medicineName, medicineDosage);
            if (!validMedicine) {
                return "Medicine name " + medicine + " is invalid. Please enter the correct info and try again.";
            }
        }

        Date expDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String requestDate = (String) reqParameters.get("expDate");
        if (commonService.isStringDate(requestDate)) {
            try {
                expDate = format.parse(requestDate);
            } catch (ParseException e) {
                logger.error("Can't parse request parameter ExpDate: " + requestDate + ". Error: " + e);
            }
        } else {
            return "Next date for recipe is invalid:" + requestDate + " (min 2 days, max 2 month). Please enter the correct info and try again.";
        }

        boolean validDate = isDateValid(expDate);

        if (validDate) {
            recipeService.doctorInsertRecipe(medicineName, medicineDosage, accountId, customerId, expDate);
        } else {
            return "Next date for recipe is invalid:" + requestDate + ". Please enter the correct info and try again.";
        }
        return errorString;
    }

    /**
     * The method for checking is the date from request valid. The minimum date mast be Today+2,
     * the maximum date mast by Today+60. Uses {@link CommonService#getFutureDate(int)}
     *
     * @param requestDate - request date from jsp
     * @return - true if the date is correct
     */
    private boolean isDateValid(Date requestDate) {
        Date minValidDate = commonService.getFutureDate(2);
        Date maxValidDate = commonService.getFutureDate(60);
        if (requestDate.compareTo(minValidDate) > 0 && requestDate.compareTo(maxValidDate) < 0) {
            return true;
        }
        return false;
    }


}
