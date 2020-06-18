package by.petropavlovskaja.pharmacy.model.account;

import by.petropavlovskaja.pharmacy.model.Medicine;
import by.petropavlovskaja.pharmacy.model.MedicineInOrder;
import by.petropavlovskaja.pharmacy.model.Order;
import by.petropavlovskaja.pharmacy.model.Recipe;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Customer extends Account implements Serializable {
    private static final long serialVersionUID = -1256423013564120517L;
    private int balance;
    private int balanceRub; // currency unit
    private int balanceCoin; // currency unit
    private int credit;
    private int creditRub; // currency unit
    private int creditCoin; // currency unit

    private Map<Order, Set<MedicineInOrder>> ordersWithDetails = new HashMap<>();   // history
    private Order cart = new Order(-1);                                 // cart
    private Set<MedicineInOrder> medicineInCart = new HashSet<>();          // medicine in cart
    private Set<Medicine> favouriteMedicines = new TreeSet<>();           // favorite
    private Set<Recipe> recipes = new TreeSet<>();                        // recipe


    public Customer(int id) {
        super(id);
    }

    public Customer(AccountBuilder accountBuilder, int balance, int credit) {
        super(accountBuilder);
        this.balance = balance;
        this.balanceRub = balance / 100;
        this.balanceCoin = balance % 100;
        this.credit = credit;
        this.creditRub = credit / 100;
        this.creditCoin = credit % 100;
    }

    public int getBalanceRub() {
        return balanceRub;
    }

    public int getBalanceCoin() {
        return balanceCoin;
    }

    public int getCreditRub() {
        return creditRub;
    }

    public int getCreditCoin() {
        return creditCoin;
    }

    public Set<Recipe> getRecipes() {
        return recipes;
    }

    public int getBalance() {
        return balance;
    }

    public int getCredit() {
        return credit;
    }

    public Map<Order, Set<MedicineInOrder>> getOrdersWithDetails() {
        return ordersWithDetails;
    }

    public Order getCart() {
        return cart;
    }

    public Set<MedicineInOrder> getMedicineInCart() {
        return medicineInCart;
    }

    public Set<Medicine> getFavouriteMedicines() {
        return favouriteMedicines;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public void setOrdersWithDetails(Map<Order, Set<MedicineInOrder>> ordersWithDetails) {
        this.ordersWithDetails = ordersWithDetails;
    }

    public void setCart(Order cart) {
        this.cart = cart;
    }

    public void setCartMedicine(Set<MedicineInOrder> medicineInCart) {
        this.medicineInCart = medicineInCart;
    }

    public void setFavouriteMedicines(Set<Medicine> favouriteMedicines) {
        this.favouriteMedicines = favouriteMedicines;
    }

    public void setRecipes(Set<Recipe> recipes) {
        this.recipes = recipes;
    }

    public void addMedicineIntoCart(MedicineInOrder newMedicine) {
        medicineInCart.add(newMedicine);
    }

    @Override
    public String toString() {
        return super.toString() +
                ", balance=" + balance +
                ", credit=" + credit;
    }
}
