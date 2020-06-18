package by.petropavlovskaja.pharmacy.service;

import by.petropavlovskaja.pharmacy.dao.AccountDAO;
import by.petropavlovskaja.pharmacy.dao.MedicineDAO;
import by.petropavlovskaja.pharmacy.model.Medicine;
import by.petropavlovskaja.pharmacy.model.Recipe;
import by.petropavlovskaja.pharmacy.model.account.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class DoctorService {
    private static Logger logger = LoggerFactory.getLogger(DoctorService.class);
    private RecipeService recipeService = RecipeService.getInstance();
    private AccountDAO accountDAO = AccountDAO.getInstance();
    private MedicineDAO medicineDAO = MedicineDAO.getInstance();

    private DoctorService() {
    }

    private static class DoctorServiceHolder {
        public static final DoctorService DOCTOR_SERVICE = new DoctorService();
    }

    public static DoctorService getInstance() {
        return DoctorServiceHolder.DOCTOR_SERVICE;
    }

    public Set<Recipe> getOrderedRecipe() {
        return recipeService.getAllOrdered();
    }

    public void deleteRecipe(Map<String, Object> reqParameters) {
        int recipeId = Integer.parseInt((String) reqParameters.get("recipeId"));
        recipeService.doctorDeleteRecipe(recipeId);
    }

    public Map<Integer, String> getActiveCustomers() {
        Set<Account> accountSet = accountDAO.getActiveCustomers();
        Map<Integer, String> customerMap = new TreeMap<>();
        for (Account account : accountSet) {
            customerMap.put(account.getId(), (account.getSurname() + " " + account.getName() + " " + account.getPatronymic() + " " +
                    account.getPhoneNumber()));
        }
        return customerMap;
    }

    public Set<Medicine> getAvailableMedicine() {
        return medicineDAO.getAllForDoctor();
    }

    public void createRecipe(int accountId, Map<String, Object> reqParameters) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String requestDate = (String) reqParameters.get("exp_date");
        Date exp_date = null;
        try {
            exp_date = format.parse(requestDate);
        } catch (ParseException e) {
            logger.error("Can't parse request parameter Exp_date: " + exp_date + ". Error: " + e);
        }

        String[] request;
        String medicine = (String) reqParameters.get("medicine");
        String customer = (String) reqParameters.get("customer");

        request = medicine.split(",");
        String medicineName = request[0].trim();
        String medicineDosage = request[1].trim();

        request = customer.split(",");
        int customerId = Integer.parseInt(request[1].trim());

        recipeService.doctorInsertRecipe(medicineName, medicineDosage, accountId, customerId, exp_date);
    }
}
