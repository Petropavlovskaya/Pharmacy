package by.petropavlovskaja.pharmacy.service;

import by.petropavlovskaja.pharmacy.EntityRepository;
import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.dao.MedicineDAO;
import by.petropavlovskaja.pharmacy.model.Medicine;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.swing.text.html.parser.Entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PharmacistServiceTest extends EntityRepository {

    @Test
    void getInstance() {
        assertNotNull(PharmacistService.getInstance());
        assertEquals(PharmacistService.getInstance(), PharmacistService.getInstance());
    }

    @Test
    void changeMedicineInDB() {
        MedicineDAO mocDAO = Mockito.mock(MedicineDAO.class);
        when(mocDAO.update(Mockito.any())).thenReturn(true);
        PharmacistService mocService = Mockito.mock(PharmacistService.class);
        when(mocService.createTempMedicine(Mockito.anyMap(), Mockito.anyBoolean(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(MEDICINE1);
        Map<String, Object> emptyMap = new HashMap<>();
        String error = "noError";

        boolean insertResult = false;
        String frontRub = "2";
        String frontKop = "99";
        if (CommonService.getInstance().isPriceNumber(frontRub, frontKop)) {
            int rub = Integer.parseInt(frontRub);
            int kop = Integer.parseInt(frontKop);
            Medicine tempMedicine = mocService.createTempMedicine(emptyMap, false, rub, kop);
            if (tempMedicine.getId() != -1) {
                String errorMessage = PharmacistService.getInstance().checkMedicineDataBeforeCreate(tempMedicine, rub, kop);
                if (errorMessage != null) {
                    error = "The changes have not saved. " + errorMessage;
                } else {
                    insertResult = mocDAO.update(tempMedicine);
                }
            } else {
                error = "Medicine hasn't created. One or more parameters are incorrect.";
            }
        }
        assertTrue(insertResult);
        assertEquals("noError", error);

    }

    @Test
    void findMedicineById() {
        MedicineDAO moc = Mockito.mock(MedicineDAO.class);
        when(moc.findById(Mockito.anyInt())).thenReturn(MEDICINE2);

        String error = "noError";
        Medicine medicine = new Medicine(-1);
        int medicineId = Integer.parseInt("8");
        if (medicineId > 0) {
            medicine = moc.findById(medicineId);
        } else {
            error = "Medicine ID can't be less than 1.";
        }

        assertEquals("noError", error);
        assertEquals(MEDICINE2, medicine);
    }

    @Test
    void deleteMedicineFromDB() {
        MedicineDAO mocDao = Mockito.mock(MedicineDAO.class);
        PharmacistService mocService = Mockito.mock(PharmacistService.class);
        when(mocService.findMedicineById(Mockito.anyMap(), Mockito.any())).thenReturn(MEDICINE2);
        when(mocDao.deleteById(Mockito.any(), Mockito.anyString())).thenReturn(true).thenReturn(false);

        Map<String, Object> reqParameters = new HashMap<>();
        String pharmacistLogin = "accountLogin";
        Medicine medicine = mocService.findMedicineById(reqParameters, new ExecuteResult());

        String error = "noError";
        boolean result = mocDao.deleteById(medicine, pharmacistLogin);
        if (!result) {
            error = "The changes have not saved. " + medicine.getName();
        }

        assertEquals("noError", error);
        assertTrue(result);

        result = mocDao.deleteById(medicine, pharmacistLogin);
        if (!result) {
            error = "The changes have not saved. " + medicine.getName();
        }

        assertTrue(error.contains("The changes have not saved."));
        assertFalse(result);
    }

    @Test
    void addMedicineIntoDB() {
        Map<String, Object> reqParameters = new HashMap<>();
        reqParameters.put("priceRub", "2b");                 // first invalid
        reqParameters.put("priceKop", "89");
        reqParameters.put("medicineName", "None");
        reqParameters.put("indivisibleAmount", "33w");      // second invalid
        reqParameters.put("amount", "850");
        reqParameters.put("dosage", "-");
        reqParameters.put("expDate", "2020-07-11");         // third invalid
        reqParameters.put("accountId", "3");
        reqParameters.put("pharmForm", "some");
        reqParameters.put("medicineId", "20");

        // first invalid
//        assertFalse(PharmacistService.getInstance().addMedicineIntoDB(reqParameters, new ExecuteResult()));

        // first valid, second invalid
        reqParameters.replace("priceRub", "2b", "2");
//        assertFalse(PharmacistService.getInstance().addMedicineIntoDB(reqParameters, new ExecuteResult()));

        // second valid, third invalid (if third valid - method calls real DAO layer)
        reqParameters.replace("indivisibleAmount", "33w", "33");
//        assertFalse(PharmacistService.getInstance().addMedicineIntoDB(reqParameters, new ExecuteResult()));
    }

    @Test
    void createTempMedicine() {
        Map<String, Object> reqParameters = new HashMap<>();
        reqParameters.put("indivisibleAmount", "33w");
        reqParameters.put("amount", "8");

        Medicine medicine = PharmacistService.getInstance().createTempMedicine(reqParameters, false, 5, 55);
        assertEquals(new Medicine(-1).getId(), medicine.getId());

        reqParameters.replace("indivisibleAmount", "33w", "33");
        reqParameters.put("amount", "-");
        medicine = PharmacistService.getInstance().createTempMedicine(reqParameters, false, 5, 55);
        assertEquals(new Medicine(-1).getId(), medicine.getId());
    }

    @Test
    void checkMedicineDataBeforeCreate() {
        Medicine medicine = MEDICINE1;
        assertNull(PharmacistService.getInstance().checkMedicineDataBeforeCreate(medicine, 55, 8));
        assertNotNull(PharmacistService.getInstance().checkMedicineDataBeforeCreate(medicine, 55, 102));
        assertNotNull(PharmacistService.getInstance().checkMedicineDataBeforeCreate(medicine, 1000, 12));
    }

    @Test
    void medicineNameMatchRegex() {
        assertTrue(PharmacistService.getInstance().medicineNameMatchRegex("A-dsfd"));
        assertTrue(PharmacistService.getInstance().medicineNameMatchRegex("Adsfd"));
        assertTrue(PharmacistService.getInstance().medicineNameMatchRegex("Af dsfd"));
        assertTrue(PharmacistService.getInstance().medicineNameMatchRegex("A dsfd sdd-sd"));
        assertTrue(PharmacistService.getInstance().medicineNameMatchRegex("A-dsfd sdd-sd"));
        assertTrue(PharmacistService.getInstance().medicineNameMatchRegex("A dsfd"));
        assertTrue(PharmacistService.getInstance().medicineNameMatchRegex("A-Asfd"));
        assertFalse(PharmacistService.getInstance().medicineNameMatchRegex("Af- -dsfd"));
        assertFalse(PharmacistService.getInstance().medicineNameMatchRegex("tf- -dsfd"));
        assertFalse(PharmacistService.getInstance().medicineNameMatchRegex("Af--dsfd"));
        assertFalse(PharmacistService.getInstance().medicineNameMatchRegex("A dsfd sdd-sd*"));
        assertFalse(PharmacistService.getInstance().medicineNameMatchRegex("A#dsfd"));


//        String regex = "(([A-ZА-Я][a-zа-я]{1,20})([-\\s][A-ZА-Я][a-zа-я]{1,20})*)+?";
//        String regex = "(([A-ZА-Я][a-zа-я]{1,10})[-\\s]*)+?(([a-zа-я]{1,10})[-\\s]*)+?";
    }

}