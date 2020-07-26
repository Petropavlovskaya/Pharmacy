package by.petropavlovskaja.pharmacy.service;

import by.petropavlovskaja.pharmacy.EntityRepository;
import by.petropavlovskaja.pharmacy.Repository;
import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.dao.AccountDAO;
import by.petropavlovskaja.pharmacy.dao.MedicineDAO;
import by.petropavlovskaja.pharmacy.dao.MedicineInOrderDAO;
import by.petropavlovskaja.pharmacy.dao.OrderDAO;
import by.petropavlovskaja.pharmacy.model.Medicine;
import by.petropavlovskaja.pharmacy.model.MedicineInOrder;
import by.petropavlovskaja.pharmacy.model.Order;
import by.petropavlovskaja.pharmacy.model.Recipe;
import by.petropavlovskaja.pharmacy.model.account.Account;
import by.petropavlovskaja.pharmacy.model.account.AccountRole;
import by.petropavlovskaja.pharmacy.model.account.Customer;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.sql.DataTruncation;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.booleanThat;
import static org.mockito.ArgumentMatchers.floatThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class CustomerServiceTest extends EntityRepository {
    Customer testCustomer = new Customer(new Account.AccountBuilder("Zobnin", "Eugeny", AccountRole.CUSTOMER)
            .withId(2).withPatronymic("Yuryevich").withPhoneNumber("+375(33)3336687").withStatus(false));
    Medicine testMedicine1 = new Medicine(1, "Парацетамол", 1, 39, "30 мг/мл", getDate("2021-08-08"), false, 299, 3, "сироп");
    Medicine testMedicine2 = new Medicine(2, "Гроприносин", 10, 111, "500 мг", getDate("2022-01-29"), true, 460, 41, "таблетки");

    CustomerServiceTest() throws ParseException {
    }

    private Date getDate(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.parse(date);
    }

    @Test
    void getInstance() {
        assertNotNull(CommonService.getInstance());
    }

    @Test
    void addMedicineInOrder() {
        Map<String, Object> reqParameters = new HashMap<>();
        reqParameters.put("amountForBuy", "7d00");

        assertFalse(CustomerService.getInstance().addMedicineInOrder(new Customer(-1), reqParameters));

        reqParameters.replace("amountForBuy", "0");
        assertFalse(CustomerService.getInstance().addMedicineInOrder(new Customer(-1), reqParameters));
    }

    @Test
    void updateQuantityInCart() {
        Map<String, Object> reqParameters = new HashMap<>();
        reqParameters.put("amountForBuy", "7d00");
        assertFalse(CustomerService.getInstance().addMedicineInOrder(new Customer(-1), reqParameters));

        reqParameters.replace("amountForBuy", "0");
        assertFalse(CustomerService.getInstance().addMedicineInOrder(new Customer(-1), reqParameters));
    }

    @Test
    void findMedicineById() {
        MedicineDAO moc = Mockito.mock(MedicineDAO.class);
        when(moc.findById(Mockito.anyInt())).thenReturn(testMedicine1);

        Medicine medicine = new Medicine(-1);
        int medicineId = Integer.parseInt("1");
        Medicine dbMedicine = moc.findById(medicineId);
        if (dbMedicine != null) {
            medicine = dbMedicine;
        }
        assertEquals(testMedicine1, medicine);
    }

    @Test
    void getAllOrdersWithDetails() {
        Map<Order, Set<MedicineInOrder>> testOrders = new HashMap<>();
        Set<MedicineInOrder> medicineSet = new HashSet<>();
        medicineSet.add(MEDICINE_IN_ORDER1);
        medicineSet.add(MEDICINE_IN_ORDER2);
        testOrders.put(new Order(1), medicineSet);

        medicineSet.add(MEDICINE_IN_ORDER3);
        testOrders.put(new Order(2), medicineSet);

        OrderDAO moc = Mockito.mock(OrderDAO.class);
        when(moc.getAllOrdersWithDetails(Mockito.anyInt())).thenReturn(testOrders);

        Map<Order, Set<MedicineInOrder>> orders = moc.getAllOrdersWithDetails(5);
        testCustomer.setOrdersWithDetails(orders);

        assertEquals(2, testCustomer.getOrdersWithDetails().size());
        assertEquals(2, testCustomer.getOrdersWithDetails().values().size());
    }

    @Test
    void deleteMedicineFromCart() {
        Set<MedicineInOrder> testSet = new HashSet<>();
        testSet.add(MEDICINE_IN_ORDER1);
        testSet.add(MEDICINE_IN_ORDER2);
        testSet.add(MEDICINE_IN_ORDER3);
        MedicineInOrderDAO moc = Mockito.mock(MedicineInOrderDAO.class);
        doNothing().when(moc).deleteMedicineFromOrder(Mockito.anyInt());
        String frontMedicineId = "2v";
        if (CommonService.getInstance().isNumber(frontMedicineId)) {
            int medicineId = Integer.parseInt(frontMedicineId);
            moc.deleteMedicineFromOrder(medicineId);
            testSet.remove(MEDICINE_IN_ORDER2);
        }
        assertEquals(3, testSet.size());

        frontMedicineId = "10";
        if (CommonService.getInstance().isNumber(frontMedicineId)) {
            int medicineId = Integer.parseInt(frontMedicineId);
            moc.deleteMedicineFromOrder(medicineId);
            testSet.remove(MEDICINE_IN_ORDER2);
        }
        assertEquals(2, testSet.size());
    }

    @Test
    void checkAvailableRecipe() {
        Set<Recipe> recipeSet = new HashSet<>();
        recipeSet.add(RECIPE1);
        recipeSet.add(RECIPE2);
        recipeSet.add(RECIPE3);
        List<Medicine> medicineList = new ArrayList<>();
        medicineList.add(MEDICINE1);
        medicineList.add(MEDICINE2);
        medicineList.add(MEDICINE3);
        medicineList.add(MEDICINE4);
        RecipeService moc = Mockito.mock(RecipeService.class);
        when(moc.getValidRecipes(Mockito.any())).thenReturn(recipeSet);

        Set<Recipe> recipes = moc.getValidRecipes(new Customer(1));
        for (Medicine medItem : medicineList) {
            if (medItem.isRecipeRequired()) {
                for (Recipe recipeItem : recipes) {
                    if (medItem.getName().equals(recipeItem.getMedicine()) && medItem.getDosage().equals(recipeItem.getDosage())) {
                        medItem.setCustomerNeedRecipe(false);
                        break;
                    } else {
                        medItem.setCustomerNeedRecipe(true);
                    }
                }
            }
        }

        assertFalse(medicineList.get(1).isCustomerNeedRecipe());
        assertTrue(medicineList.get(3).isCustomerNeedRecipe());
    }

    @Test
    void increaseBalance() {
        Map<String, Object> reqParameters = new HashMap<>();
        reqParameters.put("balance", "7d00");
        assertFalse(CustomerService.getInstance().increaseBalance(new Customer(-1), reqParameters, new ExecuteResult()));

        reqParameters.put("balance", "0");
        assertFalse(CustomerService.getInstance().increaseBalance(new Customer(-1), reqParameters, new ExecuteResult()));

        reqParameters.put("balance", "10000");
        assertFalse(CustomerService.getInstance().increaseBalance(new Customer(-1), reqParameters, new ExecuteResult()));
    }
}