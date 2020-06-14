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

public class PharmacistService {
    private static Logger logger = LoggerFactory.getLogger(PharmacistService.class);
    private AccountDAO accountDAO = AccountDAO.getInstance();
    private MedicineDAO medicineDAO = MedicineDAO.getInstance();
    private OrderDAO orderDAO = OrderDAO.getInstance();

    private PharmacistService() {
    }

    private static class PharmacistServiceHolder {
        public static final PharmacistService PHARMACIST_SERVICE = new PharmacistService();
    }

    public static PharmacistService getInstance() {
        return PharmacistServiceHolder.PHARMACIST_SERVICE;
    }

    public boolean changeMedicineInDB(Map<String, Object> reqParameters, ExecuteResult executeResult) {
        boolean insertResult = false;
        int rub = Integer.parseInt((String) reqParameters.get("price_rub"));
        int kop = Integer.parseInt((String) reqParameters.get("price_kop"));

        Medicine tempMedicine = createTempMedicine(reqParameters);

        String errorMessage = checkMedicineDataBeforeCreate(tempMedicine, rub, kop);
        if (errorMessage != null) {
            executeResult.setResponseAttributes("message", errorMessage);
        } else {

            logger.info(" Create Medicine with param: " + tempMedicine.toString());
            insertResult = MedicineDAO.getInstance().update(tempMedicine);
        }
        return insertResult;

    }

    public Medicine findMedicineById(Map<String, Object> reqParameters, ExecuteResult executeResult) {
        Medicine medicine = new Medicine(-1);
        int medicineId = Integer.parseInt((String) reqParameters.get("medicine_id"));
        String pharmacistLogin = (String) reqParameters.get("accountLogin");

        System.out.println("PharmacistService. Try to find medicine by ID = " + medicineId);

        if (medicineId > 0) {
            medicine = medicineDAO.findById(medicineId);
            System.out.println("PharmacistService. Found medicine by ID = " + medicine.toString());
        } else {
            executeResult.setResponseAttributes("message", "Medicine ID can't be less than 1.");
        }

        return medicine;
    }

    public boolean deleteMedicineFromDB(Map<String, Object> reqParameters, ExecuteResult executeResult) {
        Medicine medicine;

        String medicine_id = (String) reqParameters.get("medicine_id");
        String pharmacistLogin = (String) reqParameters.get("accountLogin");

        medicine = findMedicineById(reqParameters, executeResult);

        System.out.println("PharmacistService. Try to delete medicine by ID = " + medicine_id);
        boolean result = medicineDAO.deleteById(medicine, pharmacistLogin);
        System.out.println("PharmacistService. Delete medicine by ID = " + medicine.toString());
        if (!result) {
            executeResult.setResponseAttributes("message", "Can't delete medicine: " + medicine.toString());
        }

        return result;
    }

    public boolean addMedicineIntoDB(Map<String, Object> reqParameters, ExecuteResult executeResult) {
// in future check if this medicine already exist
        boolean insertResult = false;
        int rub = Integer.parseInt((String) reqParameters.get("price_rub"));
        int kop = Integer.parseInt((String) reqParameters.get("price_kop"));

        Medicine tempMedicine = createTempMedicine(reqParameters);

        String errorMessage = checkMedicineDataBeforeCreate(tempMedicine, rub, kop);
        if (errorMessage != null) {
            executeResult.setResponseAttributes("message", errorMessage);
        } else {

            logger.info(" Create Medicine with param: " + tempMedicine.toString());
            insertResult = MedicineDAO.getInstance().create(tempMedicine);
        }
        return insertResult;
    }

    private Medicine createTempMedicine(Map<String, Object> reqParameters) {
        Medicine tempMedicine;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String requestDate = (String) reqParameters.get("exp_date");
        int rub = Integer.parseInt((String) reqParameters.get("price_rub"));
        int kop = Integer.parseInt((String) reqParameters.get("price_kop"));

        int id = Integer.parseInt((String) reqParameters.get("medicine_id"));
        String medicineName = (String) reqParameters.get("medicine_name");
        int indivisible_amount = Integer.parseInt((String) reqParameters.get("indivisible_amount"));
        int amount = Integer.parseInt((String) reqParameters.get("amount"));
        String dosage = (String) reqParameters.get("dosage");
        Date exp_date = null;
        try {
            exp_date = format.parse(requestDate);
        } catch (ParseException e) {
            logger.error("Can't parse request parameter Exp_date: " + exp_date + ". Error: " + e);
        }
        boolean recipe_required = false;
        if (reqParameters.get("recipe_required") != null) {
            recipe_required = true;
        }
        int price = rub * 100 + kop;
        int added_by = Integer.parseInt((String) reqParameters.get("accountId"));
        String pharm_form = (String) reqParameters.get("pharm_form");

        tempMedicine = new Medicine(id, medicineName, indivisible_amount, amount, dosage, exp_date, recipe_required, price, added_by, pharm_form);

        return tempMedicine;
    }

    private String checkMedicineDataBeforeCreate(Medicine medicine, int rub, int kop) {
        String result = null;
        Date currentDate = new Date();
        if (medicine.getName().length() < 1) {
            result = "Field Medicine name can't by empty.";
        } else if (rub > 999) {
            result = "Limit for price is 999 rubley. If you need max contact with your administrator.";
        } else if (kop > 99) {
            result = "Coins can't be more than 99!.";
        } else if (medicine.getAmount() % medicine.getIndivisible_amount() != 0) {
            result = "Amount don't divide for indivisible amount. Set right amount and try again.";
        } else if (medicine.getDosage().length() < 1) {
            result = "Field Medicine dosage can't by empty.";
        } else if (currentDate.after(medicine.getExp_date())) {
            result = "Expiry date can't be earlier than today.";
        } else if (medicine.getPrice() < 5) {
            result = "Minimum price is 5 coins. If you need min contact with your administrator.";
        } else if (medicine.getPharm_form().length() < 1) {
            result = "Field Pharmaceutical form can't by empty.";
        }
        return result;
    }

}


