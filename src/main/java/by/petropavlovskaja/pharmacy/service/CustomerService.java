package by.petropavlovskaja.pharmacy.service;

import by.petropavlovskaja.pharmacy.dao.AccountDAO;
import by.petropavlovskaja.pharmacy.dao.MedicineDAO;
import by.petropavlovskaja.pharmacy.dao.OrderDAO;
import by.petropavlovskaja.pharmacy.dao.RecipeDAO;
import by.petropavlovskaja.pharmacy.model.Medicine;
import by.petropavlovskaja.pharmacy.model.MedicineInOrder;
import by.petropavlovskaja.pharmacy.model.Order;
import by.petropavlovskaja.pharmacy.model.Recipe;
import by.petropavlovskaja.pharmacy.model.account.Account;
import by.petropavlovskaja.pharmacy.model.account.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class CustomerService {
    private static Logger logger = LoggerFactory.getLogger(CustomerService.class);
    private AccountDAO accountDAO = AccountDAO.getInstance();
    private MedicineDAO medicineDAO = MedicineDAO.getInstance();
    private RecipeService recipeService = RecipeService.getInstance();
    private OrderDAO orderDAO = OrderDAO.getInstance();

    private CustomerService() {
    }

    private static class CustomerServiceHolder {
        public static final CustomerService CUSTOMER_SERVICE = new CustomerService();
    }

    public static CustomerService getInstance() {
        return CustomerServiceHolder.CUSTOMER_SERVICE;
    }

    public void addMedicineToFavourite() {
    }

    public void setOrdersWithDetailsFromDbToCustomerMap(Customer customer) {
        Map<Order, Set<MedicineInOrder>> ordersWithDetails = orderDAO.getCustomerOrdersWithDetails(customer.getId());
        customer.setOrdersWithDetails(ordersWithDetails);
    }

    public void addMedicineInOrder(Customer customer, Map<String, Object> reqParameters) {
        System.out.println("req params: " + reqParameters.toString());
        int amountForBuy = Integer.parseInt((String) reqParameters.get("amountForBuy"));
        System.out.println("amountForBuy: " + amountForBuy);
        if (amountForBuy > 0) {
            Order cart = findCart(customer.getId());
            int orderId = cart.getId();
            Medicine medicine = findMedicineById(reqParameters);
            int idPresentMedicineInOrder = checkMedicineForPresentInOrder(medicine, orderId);
            if (idPresentMedicineInOrder == -1) {
                int idInsertMedicine = orderDAO.createMedicineInOrder(orderId, medicine, amountForBuy);        // add Medicine to DB
                if (idInsertMedicine == -1) {
                    logger.error("Cant't get ID for insert Medicine " + medicine.toString() + " into Cart.");
                } else {
                    orderDAO.createActiveMedicineRelation(medicine.getId(), idInsertMedicine);
                }
            } else {
                orderDAO.updateMedicineInCart(idPresentMedicineInOrder, amountForBuy);
            }
        }
    }

    public void updateQuantityInCart(Customer customer, Map<String, Object> reqParameters) {
        int amountForBuy = Integer.parseInt((String) reqParameters.get("amountForBuy"));
        int medicineId = Integer.parseInt((String) reqParameters.get("medicine_id"));
        if (amountForBuy > 0) {
            orderDAO.updateMedicineInCart(medicineId, amountForBuy);
        }
        updateCartWithDetails(customer);

    }

    // If Medicine exist function return ID  Medicine in Order, else "-1"
    private int checkMedicineForPresentInOrder(Medicine medicine, int orderId) {
        int result = -1;
        MedicineInOrder medicineInOrder = orderDAO.findMedicineInOrderByMedicine(medicine, orderId);
        if (medicineInOrder.getId() != -1) {
            boolean theSame = medicine.getName().equals(medicineInOrder.getMedicine()) &&
                    medicine.getDosage().equals(medicineInOrder.getDosage()) &&
                    medicine.getIndivisible_amount() == medicineInOrder.getIndivisible_amount();
            if (theSame) {
                result = medicineInOrder.getId();
            }
        }
        return result;
    }

    public void updateCartWithDetails(Customer customer) {
        Order cart = findCart(customer.getId());
        Set<MedicineInOrder> medicineInCart = findActualPriceAndSetToCartInDB(cart.getId());
        blockMedicineWithoutRecipe(customer, medicineInCart, cart.getId());
        customer.setCartMedicine(medicineInCart);
        updateTotalPriceInDB(cart.getId(), medicineInCart);
        cart = findCart(customer.getId());
        customer.setCart(cart);

    }

    private void blockMedicineWithoutRecipe(Customer customer, Set<MedicineInOrder> medicineInCart, int cartId) {
        Set<Recipe> recipes = recipeService.getValidRecipes(customer);

        for (MedicineInOrder medicineItem : medicineInCart) {
            boolean needUpdateDb = false;
            // If medicine require recipe - find recipe
            if (medicineItem.isRecipe_required()) {
                needUpdateDb = true;
                for (Recipe recipeItem : recipes) {
                    if ((recipeItem.getMedicine().equals(medicineItem.getMedicine()) &&
                            recipeItem.getDosage().equals(medicineItem.getDosage()))) {
                        needUpdateDb = false;
                    }
                }
            }
            if (needUpdateDb) {
                medicineItem.setPriceForOne(0);
                medicineItem.setPriceForQuantity(0);
                medicineItem.setRubForQuantity(0);
                medicineItem.setCoinForQuantity(0);
            }
        }
        orderDAO.updateMedicinePriceInCart(cartId, medicineInCart);


    }

    private Set<MedicineInOrder> findActualPriceAndSetToCartInDB(int cartId) {
        Set<MedicineInOrder> medicineInCartWithActualPrice = orderDAO.findMedicineInCartWithActualPrice(cartId);
        orderDAO.updateMedicinePriceInCart(cartId, medicineInCartWithActualPrice);
        return medicineInCartWithActualPrice;
    }

    private void updateTotalPriceInDB(int cartId, Set<MedicineInOrder> medicineInCartWithActualPrice) {
        int totalCartPrice = 0;
        for (MedicineInOrder medicineItem : medicineInCartWithActualPrice) {
            totalCartPrice += medicineItem.getPriceForOne() * medicineItem.getQuantity();
        }
        orderDAO.updateCart(cartId, totalCartPrice);
    }


    private Order findCart(int customerId) {
        Order cart = orderDAO.findCart(customerId);
        return cart;
    }


    public Medicine findMedicineById(Map<String, Object> reqParameters) {
        Medicine medicine = new Medicine(-1);
        int medicineId = Integer.parseInt((String) reqParameters.get("medicine_id"));
        Medicine dbMedicine = medicineDAO.findById(medicineId);
        if (dbMedicine != null) {
            medicine = dbMedicine;
        }
        return medicine;
    }

    public Account findAccount(int id) {
        return accountDAO.find(id);
    }

    public void getAllOrdersWithDetails(Customer customer) {
        Map<Order, Set<MedicineInOrder>> orders = orderDAO.getAllOrdersWithDetails(customer.getId());
        customer.setOrdersWithDetails(orders);
    }

    public Set<Medicine> getAllMedicine() {
        return medicineDAO.getAll();
    }

    private MedicineInOrder getMedicineInOrderFromMedicine(Medicine medicine, int quantity, int orderId) {
        return new MedicineInOrder(0, medicine.getName(), medicine.getIndivisible_amount(), medicine.getDosage(), medicine.isRecipe_required(), quantity, medicine.getPrice(), orderId);
    }

    public void deleteMedicineFromCart(Customer customer, Map<String, Object> reqParameters) {
        int medicineId = Integer.parseInt((String) reqParameters.get("medicine_id"));
        orderDAO.deleteMedicine(medicineId);
        updateCartWithDetails(customer);
    }

    public void checkAvailableRecipe(Customer customer, Set<Medicine> medicineSet) {
        Set<Recipe> recipes = recipeService.getValidRecipes(customer);
        for (Medicine medItem : medicineSet) {
            if (medItem.isRecipe_required()) {
                for (Recipe recipeItem : recipes) {
                    if (medItem.getName().equals(recipeItem.getMedicine()) && medItem.getDosage().equals(recipeItem.getDosage())) {
                        medItem.setCustomerNeedRecipe(false);
                    } else {
                        medItem.setCustomerNeedRecipe(true);
                    }
                }
            }
        }
    }

    public void createOrder(Customer customer){


    }

}
