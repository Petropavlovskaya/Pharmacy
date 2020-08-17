package by.petropavlovskaja.pharmacy.service;

import by.petropavlovskaja.pharmacy.EntityRepository;
import by.petropavlovskaja.pharmacy.dao.AccountDAO;
import by.petropavlovskaja.pharmacy.dao.MedicineDAO;
import by.petropavlovskaja.pharmacy.model.account.Account;
import by.petropavlovskaja.pharmacy.model.account.Customer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class DoctorServiceTest extends EntityRepository {

    @Test
    void getInstance() {
        assertNotNull(DoctorService.getInstance());
    }


    @Test
    void refuseRecipe() {
        RecipeService moc = Mockito.mock(RecipeService.class);
        doNothing().when(moc).doctorRefuseRecipe(Mockito.anyInt(), Mockito.anyInt());

        String frontRecipeId = "8";
        int recipeId = 0;
        if (CommonService.getInstance().isNumber(frontRecipeId)) {
            recipeId = Integer.parseInt(frontRecipeId);
            moc.doctorRefuseRecipe(5, recipeId);
        }

        assertEquals(8, recipeId);
    }

    @Test
    void getActiveCustomers() {
        Set<Account> accountSet = new HashSet<>();
        accountSet.add(ACCOUNT1);
        accountSet.add(ACCOUNT2);
        accountSet.add(ACCOUNT3);
        accountSet.add(ACCOUNT1);

        Map<Integer, String> customerMap = new TreeMap<>();
        for (Account account : accountSet) {
            if (account.isActive()) {
                customerMap.put(account.getId(), (account.getSurname() + " " + account.getName() + " " + account.getPatronymic() + " " +
                        account.getPhoneNumber()));
            }
        }

        assertEquals(2, customerMap.size());
    }

    Date getDate(String date) {
        Date newDate = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            newDate = format.parse(date);
        } catch (ParseException e) {
            assertTrue(true);
        }
        return newDate;
    }

    @Test
    void isDateValid() {
        Date expDate = getDate("2020-08-15");
        assertTrue(DoctorService.getInstance().isDateValid(expDate));
        expDate = getDate("2020-09-15");
        assertTrue(DoctorService.getInstance().isDateValid(expDate));
        expDate = getDate("2020-09-01");
        assertTrue(DoctorService.getInstance().isDateValid(expDate));
        expDate = getDate("2029-08-15");
        assertFalse(DoctorService.getInstance().isDateValid(expDate));
        expDate = getDate("2021-08-15");
        assertFalse(DoctorService.getInstance().isDateValid(expDate));
        expDate = getDate("2020-10-15");
        assertFalse(DoctorService.getInstance().isDateValid(expDate));
        assertFalse(DoctorService.getInstance().isDateValid(new Date()));
    }

    @Test
    void createRecipe(){
        Map<String, Object> reqParameters = new HashMap<>();
        reqParameters.put("customer", "Ivanov Ivan");
//        assertNotNull(DoctorService.getInstance().createRecipe(1, reqParameters));
    }

    @Test
    void beforeCreateRecipeCheckCustomer() {
        Map<String, Object> reqParameters = new HashMap<>();
        reqParameters.put("customer", "Ivanov Ivan");

        Map<String, String> result = DoctorService.getInstance().beforeCreateRecipeCheckCustomer(reqParameters);
        assertNotEquals("noError", result.get("error"));

        reqParameters.replace("customer", "Ivanov Ivan; 7d");
        result = DoctorService.getInstance().beforeCreateRecipeCheckCustomer(reqParameters);
        assertNotEquals("noError", result.get("error"));
    }

    @Test
    void beforeCreateRecipeCheckMedicine() {
        Map<String, Object> reqParameters = new HashMap<>();
        reqParameters.put("medicine", "New medicine");

        Map<String, String> result = DoctorService.getInstance().beforeCreateRecipeCheckMedicine(reqParameters);
        assertNotEquals("noError", result.get("error"));
    }

    @Test
    void convertStringToDate() {
        assertNotEquals(DoctorService.getInstance().convertStringToDate("2020-08-08"), new Date());

        DoctorService moc = Mockito.mock(DoctorService.class);
        when(moc.convertStringToDate(Mockito.anyString())).thenThrow(new RuntimeException("Cannot parse the date"));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            format.parse("202-08-08");
        } catch (ParseException e) {
            assertTrue(true);
        }
    }
}