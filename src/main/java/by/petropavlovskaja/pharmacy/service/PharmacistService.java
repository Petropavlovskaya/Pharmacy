package by.petropavlovskaja.pharmacy.service;

import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.dao.AccountDAO;
import by.petropavlovskaja.pharmacy.dao.MedicineDAO;
import by.petropavlovskaja.pharmacy.dao.OrderDAO;
import by.petropavlovskaja.pharmacy.model.Medicine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/** Class for services of pharmacist role. Uses {@link CommonService}, {@link MedicineDAO} */
public class PharmacistService {
    private static Logger logger = LoggerFactory.getLogger(PharmacistService.class);
    private CommonService commonService = CommonService.getInstance();
    private MedicineDAO medicineDAO = MedicineDAO.getInstance();

    /**
     * Constructor without parameters
     */
    private PharmacistService() {
    }

    /**
     * Nested class create instance of the class
     */
    private static class PharmacistServiceHolder {
        public static final PharmacistService PHARMACIST_SERVICE = new PharmacistService();
    }

    /**
     * The method for get instance of the class
     *
     * @return - class instance
     */
    public static PharmacistService getInstance() {
        return PharmacistServiceHolder.PHARMACIST_SERVICE;
    }

    /**
     * The method for changing medicine info in database. Uses {@link CommonService#isPriceNumber(String, String)},
     * {@link MedicineDAO#update(Medicine)}
     *
     * @param reqParameters - request parameters from jsp
     * @param executeResult - execute result instance
     * @return - true if process was successful
     */
    public boolean changeMedicineInDB(Map<String, Object> reqParameters, ExecuteResult executeResult) {
        boolean insertResult = false;
        String frontRub = (String) reqParameters.get("priceRub");
        String frontKop = (String) reqParameters.get("priceKop");
        if (commonService.isPriceNumber(frontRub, frontKop)) {
            int rub = Integer.parseInt(frontRub);
            int kop = Integer.parseInt(frontKop);
            Medicine tempMedicine = createTempMedicine(reqParameters, false, rub, kop);

            String errorMessage = checkMedicineDataBeforeCreate(tempMedicine, rub, kop);
            if (errorMessage != null) {
                executeResult.setResponseAttributes("message", errorMessage);
            } else {

                logger.info(" Create Medicine with param: " + tempMedicine.toString());
                insertResult = medicineDAO.update(tempMedicine);
            }
        }
        return insertResult;
    }

    /**
     * The method for finding medicine by ID. Uses {@link MedicineDAO#findById(Integer)}
     *
     * @param reqParameters - request parameters from jsp
     * @param executeResult - execute result instance
     * @return - medicine instance if process was successful or medicine with ID=-1
     */
    public Medicine findMedicineById(Map<String, Object> reqParameters, ExecuteResult executeResult) {
        Medicine medicine = new Medicine(-1);
        int medicineId = Integer.parseInt((String) reqParameters.get("medicineId"));


        if (medicineId > 0) {
            medicine = medicineDAO.findById(medicineId);
        } else {
            executeResult.setResponseAttributes("message", "Medicine ID can't be less than 1.");
        }
        return medicine;
    }

    /**
     * The method for deleting medicine from database. Uses {@link MedicineDAO#deleteById(Medicine, String)}
     *
     * @param reqParameters - request parameters from jsp
     * @param executeResult - execute result instance
     * @return - true if process was successful
     */
    public boolean deleteMedicineFromDB(Map<String, Object> reqParameters, ExecuteResult executeResult) {
        String pharmacistLogin = (String) reqParameters.get("accountLogin");
        Medicine medicine = findMedicineById(reqParameters, executeResult);

        boolean result = medicineDAO.deleteById(medicine, pharmacistLogin);
        if (!result) {
            executeResult.setResponseAttributes("message", "Can't delete medicine: " + medicine.toString());
        }
        return result;
    }

    /**
     * The method for writing medicine into database. Uses {@link CommonService#isPriceNumber(String, String)},
     * {@link MedicineDAO#create(Medicine)}
     *
     * @param reqParameters - request parameters from jsp
     * @param executeResult - execute result instance
     * @return - true if process was successful
     */
    public boolean addMedicineIntoDB(Map<String, Object> reqParameters, ExecuteResult executeResult) {
// in future check if this medicine already exist
        boolean insertResult = false;

        String frontRub = (String) reqParameters.get("priceRub");
        String frontKop = (String) reqParameters.get("priceKop");

        if (commonService.isPriceNumber(frontRub, frontKop)) {
            int rub = Integer.parseInt(frontRub);
            int kop = Integer.parseInt(frontKop);
            Medicine tempMedicine = createTempMedicine(reqParameters, true, rub, kop);

            if (tempMedicine.getId() != -1) {
                String errorMessage = checkMedicineDataBeforeCreate(tempMedicine, rub, kop);
                if (errorMessage != null) {
                    executeResult.setResponseAttributes("message", errorMessage);
                } else {

                    logger.info(" Create Medicine with param: " + tempMedicine.toString());
                    insertResult = medicineDAO.create(tempMedicine);
                }
            } else {
                executeResult.setResponseAttributes("message", "Invalid insert data. " +
                        "Please, insert correct data and try again.");
            }
        } else {
            executeResult.setResponseAttributes("message", "Invalid insert data of price. " +
                    "Please, insert correct data and try again.");
        }
        return insertResult;
    }

    /**
     * The method for creating temporary medicine before inserting into database.
     * Uses {@link CommonService#isNumber(String)}, {@link CommonService#isStringDate(String)}
     *
     * @param reqParameters - request parameters from jsp
     * @param newMedicine - is medicine new (false for editing medicine)
     * @param rub - a rub part of medicine price
     * @param kop - a coin part of medicine price
     * @return - medicine instance if process was successful or medicine with ID=-1
     */
    private Medicine createTempMedicine(Map<String, Object> reqParameters, boolean newMedicine, int rub, int kop) {
        Medicine tempMedicine = new Medicine(-1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String requestDate = (String) reqParameters.get("expDate");
        int id = -1;

        String medicineName = String.valueOf(reqParameters.get("medicineName")).trim();

        String frontIndivisibleAmount = (String) reqParameters.get("indivisibleAmount");
        String frontAmount = (String) reqParameters.get("amount");
        if (commonService.isNumber(frontAmount) && commonService.isNumber(frontIndivisibleAmount)) {
            int indivisibleAmount = Integer.parseInt(frontIndivisibleAmount);
            int amount = Integer.parseInt(frontAmount);
            String dosage = String.valueOf(reqParameters.get("dosage")).trim();

            if (commonService.isStringDate(requestDate)) {
                Date expDate = null;
                try {
                    expDate = format.parse(requestDate);
                } catch (ParseException e) {
                    logger.error("Can't parse request parameter ExpDate: " + expDate + ". Error: " + e);
                }
                boolean recipRequired = false;
                if (reqParameters.get("recipeRequired") != null) {
                    recipRequired = true;
                }
                int price = rub * 100 + kop;
                int addedBy = Integer.parseInt((String) reqParameters.get("accountId"));
                String pharmForm = (String) reqParameters.get("pharmForm");
                id = 0;
                if (!newMedicine) {
                    id = Integer.parseInt((String) reqParameters.get("medicineId"));
                }
                tempMedicine = new Medicine(id, medicineName, indivisibleAmount, amount, dosage, expDate,
                        recipRequired, price, addedBy, pharmForm);
            }
        }
        return tempMedicine;
    }

    /**
     * The method for checking medicine information before creating.
     * Uses {@link CommonService#isNumber(String)}, {@link CommonService#isStringDate(String)}
     *
     * @param medicine - medicine instance
     * @param rub - a rub part of medicine price
     * @param kop - a coin part of medicine price
     * @return - error string or NULL
     */
    private String checkMedicineDataBeforeCreate(Medicine medicine, int rub, int kop) {
        String result = null;
        Date currentDate = new Date();
        if (medicine.getName().length() < 1) {
            result = "Field Medicine name can't by empty.";
        } else if (rub > 999) {
            result = "Limit for price is 999 rubley. If you need more contact with your administrator.";
        } else if (kop > 99) {
            result = "Coins can't be more than 99!.";
        } else if (medicine.getDosage().length() < 1) {
            result = "Field Medicine dosage can't by empty.";
        } else if (currentDate.after(medicine.getExpDate())) {
            result = "Expiry date can't be earlier than today.";
        } else if (medicine.getPrice() < 5) {
            result = "Minimum price is 5 coins. If you need less contact with your administrator.";
        } else if (medicine.getPharmForm().length() < 1) {
            result = "Field Pharmaceutical form can't by empty.";
        }
        return result;
    }
}


