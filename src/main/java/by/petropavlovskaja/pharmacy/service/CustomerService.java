package by.petropavlovskaja.pharmacy.service;

import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.dao.AccountDAO;
import by.petropavlovskaja.pharmacy.dao.MedicineDAO;
import by.petropavlovskaja.pharmacy.dao.MedicineInOrderDAO;
import by.petropavlovskaja.pharmacy.dao.OrderDAO;
import by.petropavlovskaja.pharmacy.model.Medicine;
import by.petropavlovskaja.pharmacy.model.MedicineInOrder;
import by.petropavlovskaja.pharmacy.model.Order;
import by.petropavlovskaja.pharmacy.model.Recipe;
import by.petropavlovskaja.pharmacy.model.account.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class for services of customer role. Uses {@link RecipeService}, {@link CommonService}, {@link AccountDAO},
 * {@link MedicineDAO} and {@link OrderDAO}
 */
public class CustomerService {
    private static Logger logger = LoggerFactory.getLogger(CustomerService.class);
    private RecipeService recipeService = RecipeService.getInstance();
    private CommonService commonService = CommonService.getInstance();
    private AccountDAO accountDAO = AccountDAO.getInstance();
    private MedicineDAO medicineDAO = MedicineDAO.getInstance();
    private OrderDAO orderDAO = OrderDAO.getInstance();
    private MedicineInOrderDAO medicineInOrderDAO = MedicineInOrderDAO.getInstance();

    /**
     * Constructor without parameters
     */
    private CustomerService() {
    }

    /**
     * Nested class create instance of the class
     */
    private static class CustomerServiceHolder {
        public static final CustomerService CUSTOMER_SERVICE = new CustomerService();
    }

    /**
     * The method for get instance of the class
     *
     * @return - class instance
     */
    public static CustomerService getInstance() {
        return CustomerServiceHolder.CUSTOMER_SERVICE;
    }

    /**
     * The method for writing into database record of medicine in order. Uses {@link MedicineInOrderDAO#createMedicineInOrder(int, Medicine, int)},
     * {@link OrderDAO#createActiveMedicineRelation(int, int)} ation}, {@link MedicineInOrderDAO#updateMedicineInCart(int, int)}
     *
     * @param customer      - customer instance
     * @param reqParameters - request parameters from jsp
     * @return - true if process was successful
     */
    public boolean addMedicineInOrder(Customer customer, Map<String, Object> reqParameters) {
        boolean result = false;
        String requestAmount = (String) reqParameters.get("amountForBuy");
        if (commonService.isNumber(requestAmount)) {
            int amountForBuy = Integer.parseInt((String) reqParameters.get("amountForBuy"));
            if (amountForBuy > 0) {
                Order cart = findCart(customer.getId());
                int orderId = cart.getId();
                Medicine medicine = findMedicineById(reqParameters);
                int idPresentMedicineInOrder = checkMedicineForPresentInOrder(medicine, orderId);
                if (idPresentMedicineInOrder == -1) {
                    int idInsertMedicine = medicineInOrderDAO.createMedicineInOrder(orderId, medicine, amountForBuy);        // add Medicine to DB
                    if (idInsertMedicine == -1) {
                        logger.error("Cant't get ID for insert Medicine " + medicine.toString() + " into Cart.");
                    } else {
                        orderDAO.createActiveMedicineRelation(medicine.getId(), idInsertMedicine);
                        result = true;
                    }
                } else {
                    medicineInOrderDAO.updateMedicineInCart(idPresentMedicineInOrder, amountForBuy);
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * The method for updating in database quantity of medicine in cart. Uses {@link MedicineInOrderDAO#updateMedicineInCart(int, int)}
     *
     * @param customer      - customer instance
     * @param reqParameters - request parameters from jsp
     * @return - true if process was successful
     */
    public boolean updateQuantityInCart(Customer customer, Map<String, Object> reqParameters) {
        String requestAmount = (String) reqParameters.get("amountForBuy");
        if (commonService.isNumber(requestAmount)) {
            int amountForBuy = Integer.parseInt((String) reqParameters.get("amountForBuy"));
            int medicineId = Integer.parseInt((String) reqParameters.get("medicineId"));
            if (amountForBuy > 0) {
                medicineInOrderDAO.updateMedicineInCart(medicineId, amountForBuy);
                updateCartWithDetails(customer);
                return true;
            }
//                return true;
        }
        return false;
    }

    /**
     * The method for checking is the medicine present in the order. Uses {@link MedicineInOrderDAO#findMedicineInOrderByMedicine(Medicine, int)}
     *
     * @param medicine - medicine instance
     * @param orderId  - order ID
     * @return - ID medicine if it present in the order or -1
     */
    // If Medicine exist function return ID  Medicine in Order, else "-1"
    private int checkMedicineForPresentInOrder(Medicine medicine, int orderId) {
        int result = -1;
        MedicineInOrder medicineInOrder = medicineInOrderDAO.findMedicineInOrderByMedicine(medicine, orderId);
        if (medicineInOrder.getId() != -1) {
            boolean theSame = medicine.getName().equals(medicineInOrder.getMedicine()) &&
                    medicine.getDosage().equals(medicineInOrder.getDosage()) &&
                    medicine.getIndivisibleAmount() == medicineInOrder.getIndivisibleAmount();
            if (theSame) {
                result = medicineInOrder.getId();
            }
        }
        return result;
    }

    /**
     * The method for updating cart and medicines in it
     *
     * @param customer - customer instance
     */
    public void updateCartWithDetails(Customer customer) {
        Order cart = findCart(customer.getId());
        Set<MedicineInOrder> medicineInCart = findActualPriceAndSetToCartInDB(cart.getId());
        blockMedicineWithoutRecipe(customer, medicineInCart, cart.getId());
        customer.setCartMedicine(medicineInCart);
        updateTotalPriceInDB(cart.getId(), medicineInCart);
        cart = findCart(customer.getId());
        customer.setCart(cart);
        getBalance(customer);
    }

    /**
     * The method for getting the customer balance
     *
     * @param customer - customer instance
     */
    public void getBalance(Customer customer) {
        int accountBalance = accountDAO.getCustomerBalance(customer.getId());
        customer.setBalance(accountBalance);
    }

    /**
     * The method blocks the medicine from the purchase if the customer hasn't a recipe.
     * Uses {@link RecipeService#getValidRecipes(Customer)}, {@link MedicineInOrderDAO#updateMedicinePriceInCart(int, Set)}
     *
     * @param customer       - customer instance
     * @param medicineInCart - a medicines in cart set
     * @param cartId         - a cart ID
     */
    private void blockMedicineWithoutRecipe(Customer customer, Set<MedicineInOrder> medicineInCart, int cartId) {
        Set<Recipe> recipes = recipeService.getValidRecipes(customer);

        for (MedicineInOrder medicineItem : medicineInCart) {
            boolean needUpdateDb = false;
            // If medicine require recipe - find recipe
            if (medicineItem.isRecipeRequired()) {
                needUpdateDb = true;
                for (Recipe recipeItem : recipes) {
                    if ((recipeItem.getMedicine().equals(medicineItem.getMedicine()) &&
                            recipeItem.getDosage().equals(medicineItem.getDosage()))) {
                        needUpdateDb = false;
                        break;
                    }
                }
            }
            if (needUpdateDb) {
                medicineItem.setPriceForOne(0);
                medicineItem.setRubCoin();
/*                medicineItem.setPriceForQuantity(0);
                medicineItem.setRubForQuantity(0);
                medicineItem.setCoinForQuantity(0);*/
            }
        }
        medicineInOrderDAO.updateMedicinePriceInCart(cartId, medicineInCart);
    }

    /**
     * The method gets the actual prices of medicines and sets it to customer cart into database
     * Uses {@link MedicineInOrderDAO#findMedicineInCartWithActualPrice(int, boolean)} and {@link MedicineInOrderDAO#updateMedicinePriceInCart(int, Set)}
     *
     * @param cartId - a cart ID
     * @return medicine in order set
     */
    private Set<MedicineInOrder> findActualPriceAndSetToCartInDB(int cartId) {
        Set<MedicineInOrder> medicineInCartWithActualPrice = medicineInOrderDAO.findMedicineInCartWithActualPrice(cartId, true);
        for (MedicineInOrder medicineInOrder : medicineInCartWithActualPrice) {
            if ((medicineInOrder.getAmount() != 0) && (medicineInOrder.getQuantity() > medicineInOrder.getAmount())) {
                medicineInOrder.setQuantity(medicineInOrder.getAmount());
            }
        }
        medicineInOrderDAO.updateMedicinePriceInCart(cartId, medicineInCartWithActualPrice);
        return medicineInCartWithActualPrice;
    }

    /**
     * The method for update total price in cart
     * Uses {@link OrderDAO#updateCartPrice(int, int)}
     *
     * @param cartId                        - a cart ID
     * @param medicineInCartWithActualPrice - set of medicines
     */
    private void updateTotalPriceInDB(int cartId, Set<MedicineInOrder> medicineInCartWithActualPrice) {
        int totalCartPrice = 0;
        for (MedicineInOrder medicineItem : medicineInCartWithActualPrice) {
            totalCartPrice += medicineItem.getPriceForOne() * medicineItem.getQuantity();
        }
        orderDAO.updateCartPrice(cartId, totalCartPrice);
    }

    /**
     * The method finds the customer cart by customer ID
     * Uses {@link OrderDAO#findCart(int)}
     *
     * @param customerId - a customer ID
     * @return customer cart
     */
    private Order findCart(int customerId) {
        return orderDAO.findCart(customerId);
    }

    /**
     * The method for finding medicine by medicine ID
     * Uses {@link MedicineDAO#findById(Integer)}
     *
     * @param reqParameters - a request parameters from jsp
     * @return medicine if it was found or medicine with ID=-1
     */
    public Medicine findMedicineById(Map<String, Object> reqParameters) {
        Medicine medicine = new Medicine(-1);
        int medicineId = Integer.parseInt((String) reqParameters.get("medicineId"));
        Medicine dbMedicine = medicineDAO.findById(medicineId);
        if (dbMedicine != null) {
            medicine = dbMedicine;
        }
        return medicine;
    }

    /**
     * The method for finding all customers orders with medicines in them and set them to variable
     * Uses {@link OrderDAO#getAllOrdersWithDetails(int)} and {@link Customer#setOrdersWithDetails(Map)}
     *
     * @param customer - customer instance
     */
    public void getAllOrdersWithDetails(Customer customer) {
        Map<Order, Set<MedicineInOrder>> orders = orderDAO.getAllOrdersWithDetails(customer.getId());
        customer.setOrdersWithDetails(orders);
    }

    /**
     * The method for deleting the medicine from the cart.
     * Uses {@link CommonService#isNumber(String)} and {@link MedicineInOrderDAO#deleteMedicineFromOrder(int)}
     *
     * @param customer      - customer instance
     * @param reqParameters - request parameters from jsp
     */
    public void deleteMedicineFromCart(Customer customer, Map<String, Object> reqParameters) {
        String frontMedicineId = (String) reqParameters.get("medicineId");
        if (commonService.isNumber(frontMedicineId)) {
            int medicineId = Integer.parseInt(frontMedicineId);
            medicineInOrderDAO.deleteMedicineFromOrder(medicineId);
            updateCartWithDetails(customer);
        }
    }

    /**
     * The method for checking customer's recipes for view medicine list that are available to purchased.
     * Uses {@link RecipeService#getValidRecipes(Customer)}
     *
     * @param customer     - customer instance
     * @param medicineList - medicine list
     */
    public void checkAvailableRecipe(Customer customer, List<Medicine> medicineList) {
        Set<Recipe> recipes = recipeService.getValidRecipes(customer);
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
    }

    /**
     * The method for creating the order.
     * Uses {@link OrderDAO#findCart(int)}, {@link MedicineInOrderDAO#findMedicineInCartWithActualPrice(int, boolean)},
     * {@link OrderDAO#createOrder(Customer, Order, Set)}
     *
     * @param customer - customer instance
     * @return true if update information after purchasing has successfully changed in database
     */
    public boolean createOrder(Customer customer) {
        boolean updateResult = false;
        Order cart = orderDAO.findCart(customer.getId());
        Set<MedicineInOrder> medicineInOrderSet = medicineInOrderDAO.findMedicineInCartWithActualPrice(cart.getId(), false);
        boolean resultOrderUpdate = orderDAO.createOrder(customer, cart, medicineInOrderSet);
        if (resultOrderUpdate) {
            cart = orderDAO.findCart(customer.getId());
            medicineInOrderSet = medicineInOrderDAO.findMedicineInCartWithActualPrice(cart.getId(), false);
            customer.setCartMedicine(medicineInOrderSet);
            updateResult = true;
        }
        return updateResult;
    }

    /**
     * The method for increasing customer balance.
     * Uses {@link CommonService#isNumber(String)}, {@link AccountDAO#increaseCustomerBalance(int, int)},
     *
     * @param customer      - customer instance
     * @param reqParameters - request parameters from jsp
     * @param executeResult - execute result instance
     * @return true if increasing of balance was successful
     */
    public boolean increaseBalance(Customer customer, Map<String, Object> reqParameters, ExecuteResult executeResult) {
        boolean increaseResult = false;
        String frontBalance = (String) reqParameters.get("balance");
        if (commonService.isNumber(frontBalance)) {
            int countIncrease = Integer.parseInt(frontBalance) * 100;
            int newBalance;
            if ((countIncrease > 0) && (countIncrease < 9999)) {
                newBalance = customer.getBalance() + countIncrease;
                accountDAO.increaseCustomerBalance(customer.getId(), newBalance);
                customer.setBalance(newBalance);
                increaseResult = true;
            } else {
                executeResult.setResponseAttributes("errorMessage", "The quantity of money must be more then 0 and less then 9999.");
            }
        } else {
            executeResult.setResponseAttributes("errorMessage", "Incorrect data of the money.");
        }
        return increaseResult;
    }
}
