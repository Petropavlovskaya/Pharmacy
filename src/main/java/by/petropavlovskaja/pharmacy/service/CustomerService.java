package by.petropavlovskaja.pharmacy.service;

import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.controller.session.SessionContext;
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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static by.petropavlovskaja.pharmacy.controller.AttributeConstant.*;

/**
 * Class for services of customer role. Uses {@link RecipeService}, {@link CommonService}, {@link AccountDAO},
 * {@link MedicineDAO} and {@link OrderDAO}
 */
public class CustomerService {
    private static final String AMOUNT_FOR_BYE = "amountForBuy";
    private static final String MEDICINE_ID = "medicineId";
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
     * {@link OrderDAO#createActiveMedicineRelation(int, int)} ation}, {@link MedicineInOrderDAO#updateMedicineInCart(int, int)},
     * {@link SessionContext}, {@link ExecuteResult}
     *
     * @param customer      - customer instance
     * @param reqParameters - request parameters from jsp
     * @param executeResult - ExecuteResult instance
     * @param sc            - SessionContext instance
     * @return - true if process was successful
     */
    public boolean addMedicineInOrder(Customer customer, Map<String, Object> reqParameters,
                                      ExecuteResult executeResult, SessionContext sc) {
        boolean methodExecuteResult = false;
        String requestAmount = (String) reqParameters.get(AMOUNT_FOR_BYE);
        if (commonService.isNumber(requestAmount)) {
            int amountForBuy = Integer.parseInt((String) reqParameters.get(AMOUNT_FOR_BYE));
            if (amountForBuy > 0) {
                Order cart = findCart(customer.getId());
                int orderId = cart.getId();
                Medicine medicine = findMedicineById(reqParameters);
                int idPresentMedicineInOrder = checkMedicineForPresentInOrder(medicine, orderId);
                if (idPresentMedicineInOrder == -1) {
                    int idInsertMedicine = medicineInOrderDAO.createMedicineInOrder(orderId, medicine, amountForBuy);        // add Medicine to DB
                    if (idInsertMedicine == -1) {
                        String loggerMessage = "Cant't get ID for insert Medicine " + medicine.toString() + " into Cart.";
                        logger.trace(loggerMessage);
                    } else {
                        orderDAO.createActiveMedicineRelation(medicine.getId(), idInsertMedicine);
                        methodExecuteResult = true;
                    }
                } else {
                    medicineInOrderDAO.updateMedicineInCart(idPresentMedicineInOrder, amountForBuy);
                    methodExecuteResult = true;
                }
            }
        }
        if (!methodExecuteResult) {
            executeResult.setResponseAttributes(ERROR_MSG, "Quantity of medicine for buy is incorrect. Please, try again.");
            executeResult.setJsp("/WEB-INF/jsp/customer/medicine/medicine_list.jsp");
        } else {
            sc.getSession().setAttribute(SUCCESS_MSG, "The medicine has added successfully.");
            sc.getSession().setAttribute(SUCCESS_MSG_CHECK, "yes");
        }
        return methodExecuteResult;
    }

    /**
     * The method for updating in database quantity of medicine in cart. Uses {@link MedicineInOrderDAO#updateMedicineInCart(int, int)},
     * {@link SessionContext}, {@link ExecuteResult}
     *
     * @param customer      - customer instance
     * @param reqParameters - request parameters from jsp
     * @param executeResult - ExecuteResult instance
     * @param sc            - SessionContext instance
     * @return - true if process was successful
     */
    public boolean updateQuantityInCart(Customer customer, Map<String, Object> reqParameters,
                                        ExecuteResult executeResult, SessionContext sc) {
        boolean methodExecuteResult = false;
        String requestAmount = (String) reqParameters.get(AMOUNT_FOR_BYE);
        if (commonService.isNumber(requestAmount)) {
            int amountForBuy = Integer.parseInt((String) reqParameters.get(AMOUNT_FOR_BYE));
            int medicineId = Integer.parseInt((String) reqParameters.get(MEDICINE_ID));
            if (amountForBuy > 0) {
                medicineInOrderDAO.updateMedicineInCart(medicineId, amountForBuy);
                updateCartWithDetails(customer);
                methodExecuteResult = true;
            }
        }

        if (!methodExecuteResult) {
            executeResult.setResponseAttributes(ERROR_MSG, "Quantity of medicine for buy is incorrect. Please, try again.");
            executeResult.setJsp("/WEB-INF/jsp/customer/cabinet/cabinet_cart.jsp");
        } else {
            sc.getSession().setAttribute(SUCCESS_MSG, "The medicine quantity has changed successfully.");
            sc.getSession().setAttribute(SUCCESS_MSG_CHECK, "yes");
            sc.getSession().setAttribute(MEDICINE_IN_CART, customer.getMedicineInCart());
            sc.getSession().setAttribute(CART, customer.getCart());
        }
        return methodExecuteResult;
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
        blockMedicineWithoutRecipe(customer, medicineInCart);
        blockMedicineAfterExpirationDate(medicineInCart);
        medicineInOrderDAO.updateMedicinePriceInCart(cart.getId(), medicineInCart);
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
     */
    private void blockMedicineWithoutRecipe(Customer customer, Set<MedicineInOrder> medicineInCart) {
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
            }
        }
    }

    /**
     * The method blocks the medicine from the purchase if the medicine has passed its expiration date.
     * Uses {@link RecipeService#getValidRecipes(Customer)}, {@link MedicineInOrderDAO#updateMedicinePriceInCart(int, Set)}
     *
     * @param medicineInCart - a medicines in cart set
     */
    private void blockMedicineAfterExpirationDate(Set<MedicineInOrder> medicineInCart) {
        Date currentDate = new Date();
        for (MedicineInOrder medicineItem : medicineInCart) {
            if (currentDate.after(medicineItem.getExpDate())) {
                medicineItem.setPriceForOne(0);
                medicineItem.setRubCoin();
            }
        }
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
        int medicineId = Integer.parseInt((String) reqParameters.get(MEDICINE_ID));
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
     * Uses {@link CommonService#isNumber(String)} and {@link MedicineInOrderDAO#deleteMedicineFromOrder(int)},
     * {@link SessionContext}
     *
     * @param customer      - customer instance
     * @param reqParameters - request parameters from jsp
     * @param sc            - SessionContext instance
     */
    public void deleteMedicineFromCart(Customer customer, Map<String, Object> reqParameters, SessionContext sc) {
        String frontMedicineId = (String) reqParameters.get(MEDICINE_ID);
        if (commonService.isNumber(frontMedicineId)) {
            int medicineId = Integer.parseInt(frontMedicineId);
            medicineInOrderDAO.deleteMedicineFromOrder(medicineId);
            updateCartWithDetails(customer);
            sc.getSession().setAttribute(SUCCESS_MSG, "The medicine has deleted successfully.");
            sc.getSession().setAttribute(SUCCESS_MSG_CHECK, "yes");
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
                if (!recipes.isEmpty()) {
                    for (Recipe recipeItem : recipes) {
                        if (medItem.getName().equals(recipeItem.getMedicine()) && medItem.getDosage().equals(recipeItem.getDosage())) {
                            medItem.setCustomerNeedRecipe(false);
                            break;
                        } else {
                            medItem.setCustomerNeedRecipe(true);
                        }
                    }
                } else {
                    medItem.setCustomerNeedRecipe(true);
                }
            }
        }
    }

    /**
     * The method for creating the order.
     * Uses {@link OrderDAO#findCart(int)}, {@link MedicineInOrderDAO#findMedicineInCartWithActualPrice(int, boolean)},
     * {@link OrderDAO#createOrder(Customer, Order, Set)}, {@link SessionContext}
     *
     * @param customer - customer instance
     * @param sc       - SessionContext instance
     * @return true if update information after purchasing has successfully changed in database
     */
    public boolean createOrder(Customer customer, SessionContext sc) {
        boolean updateResult = false;
        Order cart = orderDAO.findCart(customer.getId());
        Set<MedicineInOrder> medicineInOrderSet = medicineInOrderDAO.findMedicineInCartWithActualPrice(cart.getId(), false);
        boolean resultOrderUpdate = orderDAO.createOrder(customer, cart, medicineInOrderSet);
        if (resultOrderUpdate) {
            cart = orderDAO.findCart(customer.getId());
            medicineInOrderSet = medicineInOrderDAO.findMedicineInCartWithActualPrice(cart.getId(), false);
            customer.setCartMedicine(medicineInOrderSet);
            updateResult = true;
            sc.getSession().setAttribute(SUCCESS_MSG, "The purchase medicines has done successfully.");
            sc.getSession().setAttribute(SUCCESS_MSG_CHECK, "yes");
        }
        return updateResult;
    }

    /**
     * The method for increasing customer balance.
     * Uses {@link CommonService#isNumber(String)}, {@link AccountDAO#increaseCustomerBalance(int, int)}, {@link SessionContext}
     *
     * @param customer      - customer instance
     * @param reqParameters - request parameters from jsp
     * @param executeResult - execute result instance
     * @param sc            - SessionContext instance
     * @return true if increasing of balance was successful
     */
    public boolean increaseBalance(Customer customer, Map<String, Object> reqParameters, ExecuteResult executeResult,
                                   SessionContext sc) {
        boolean increaseResult = false;
        String frontBalance = (String) reqParameters.get("balance");
        if (commonService.isNumber(frontBalance)) {
            int countIncrease = Integer.parseInt(frontBalance) * 100;
            int newBalance;
            if ((countIncrease > 0) && (countIncrease < 9999)) {
                newBalance = customer.getBalance() + countIncrease;
                accountDAO.increaseCustomerBalance(customer.getId(), newBalance);
                customer.setBalance(newBalance);
                sc.getSession().setAttribute(SUCCESS_MSG, "The bill increasing has done successfully.");
                sc.getSession().setAttribute(SUCCESS_MSG_CHECK, "yes");
                increaseResult = true;
            } else {
                executeResult.setResponseAttributes(ERROR_MSG, "The quantity of money must be more then 0 and less then 9999.");
            }
        } else {
            executeResult.setResponseAttributes(ERROR_MSG, "Incorrect data of the money.");
        }
        return increaseResult;
    }

    /**
     * @param frontOrderId  - Order ID for delete from front page
     * @param executeResult - execute result instance
     * @param sc            - session context instance
     * @return - true if method was execute successful.
     */
    public boolean deleteOrder(String frontOrderId, ExecuteResult executeResult, SessionContext sc) {
        boolean executeMethodResult = false;
        boolean isOrderIdNumber = commonService.isNumber(frontOrderId);
        if (isOrderIdNumber) {
            int orderId = Integer.parseInt(frontOrderId);
            boolean resultDelete = orderDAO.deleteOrder(orderId);
            if (resultDelete) {
                sc.getSession().setAttribute(SUCCESS_MSG, "The order has deleted successfully.");
                sc.getSession().setAttribute(SUCCESS_MSG_CHECK, "yes");
                executeMethodResult = true;
            } else {
                executeResult.setResponseAttributes(ERROR_MSG, "Something went wrong. Please, contact with site administrator ");
                executeResult.setJsp("/WEB-INF/jsp/customer/cabinet/cabinet_history.jsp");
            }
        } else {
            logger.trace("Incorrect data of orderId.");
            executeResult.setResponseAttributes(ERROR_MSG, "Something went wrong. Please, contact with site administrator ");
            executeResult.setJsp("/WEB-INF/jsp/customer/cabinet/cabinet_history.jsp");
        }
        return executeMethodResult;
    }
}
