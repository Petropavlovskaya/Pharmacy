package by.petropavlovskaja.pharmacy.model.account;

import by.petropavlovskaja.pharmacy.model.MedicineInOrder;
import by.petropavlovskaja.pharmacy.model.Order;
import by.petropavlovskaja.pharmacy.model.Recipe;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class for customer entity extends {@link Account}. Has next properties:
 * <b>serialVersionUID</b>, <b>balance</b>, <b>balanceRub</b>, <b>balanceCoin</b>,
 * <b>ordersWithDetails</b>, <b>cart</b>, <b>medicineInCart</b> and <b>recipes</b>
 */
public class Customer extends Account implements Serializable {
    /**
     * Property - serial version UID
     */
    private static final long serialVersionUID = -1256423013564120517L;
    /**
     * Property - customer balance
     */
    private int balance;
    /**
     * Property - customer rub part of balance
     */
    private int balanceRub; // currency unit
    /**
     * Property - customer coin part of balance
     */
    private int balanceCoin; // currency unit

    /**
     * Property - customer orders with details
     */
    private Map<Order, Set<MedicineInOrder>> ordersWithDetails = new HashMap<>();   // history
    /**
     * Property - customer cart
     */
    private Order cart = new Order(-1);                                 // cart
    /**
     * Property - customer medicines in the cart
     */
    private Set<MedicineInOrder> medicineInCart = new HashSet<>();          // medicine in cart
    /**
     * Property - customer recipes
     */
    private Set<Recipe> recipes = new HashSet<>();                        // recipe

    /**
     * Create entity of class {@link Customer#Customer(AccountBuilder, int)}
     *
     * @param id - customer ID
     */
    public Customer(int id) {
        super(id);
    }

    /**
     * Create entity of class {@link Customer#Customer(int)}
     *
     * @param accountBuilder - account builder {@link Account.AccountBuilder}
     * @param balance        - account balance
     */
    public Customer(AccountBuilder accountBuilder, int balance) {
        super(accountBuilder);
        this.balance = balance;
        this.balanceRub = balance / 100;
        this.balanceCoin = balance % 100;
    }

    /**
     * Create entity of class {@link Customer#Customer(AccountBuilder, int)}
     *
     * @param accountBuilder - account builder {@link Account.AccountBuilder}
     */
    public Customer(AccountBuilder accountBuilder) {
        super(accountBuilder);
    }

    /**
     * The method of getting the rub part of balance value
     *
     * @return - a rub part of balance value
     */
    public int getBalanceRub() {
        return balanceRub;
    }

    /**
     * The method of getting the coin part of balance value
     *
     * @return - a coin part of balance value
     */
    public int getBalanceCoin() {
        return balanceCoin;
    }

    /**
     * The method of getting the recipes set value
     *
     * @return - a recipes set value
     */
    public Set<Recipe> getRecipes() {
        return recipes;
    }

    /**
     * The method of getting the balance value
     *
     * @return - a balance value
     */
    public int getBalance() {
        return balance;
    }

    /**
     * The method of getting the orders with details value
     *
     * @return - a orders with details value
     */
    public Map<Order, Set<MedicineInOrder>> getOrdersWithDetails() {
        return ordersWithDetails;
    }

    /**
     * The method of getting the cart value
     *
     * @return - a cart value
     */
    public Order getCart() {
        return cart;
    }

    /**
     * The method of getting the medicines in cart set value
     *
     * @return - a medicines in cart set value
     */
    public Set<MedicineInOrder> getMedicineInCart() {
        return medicineInCart;
    }

    /**
     * The method for setting the customer balance
     *
     * @param balance - the customer balance
     */
    public void setBalance(int balance) {
        this.balance = balance;
        this.balanceRub = balance / 100;
        this.balanceCoin = balance % 100;
    }

    /**
     * The method for setting the customer orders with details
     *
     * @param ordersWithDetails - the customer orders with details
     */
    public void setOrdersWithDetails(Map<Order, Set<MedicineInOrder>> ordersWithDetails) {
        this.ordersWithDetails = ordersWithDetails;
    }

    /**
     * The method for setting the customer cart
     *
     * @param cart - the customer cart
     */
    public void setCart(Order cart) {
        this.cart = cart;
    }

    /**
     * The method for setting the customer medicines in cart
     *
     * @param medicineInCart - the customer medicines in cart
     */
    public void setCartMedicine(Set<MedicineInOrder> medicineInCart) {
        this.medicineInCart = medicineInCart;
    }

    /**
     * The method for setting the customer recipes
     *
     * @param recipes - the customer recipes
     */
    public void setRecipes(Set<Recipe> recipes) {
        this.recipes = recipes;
    }

//    public void addMedicineIntoCart(MedicineInOrder newMedicine) {
//        medicineInCart.add(newMedicine);
//    }

    @Override
    public String toString() {
        return super.toString();
    }
}
