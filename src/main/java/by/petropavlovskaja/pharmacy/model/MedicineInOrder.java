package by.petropavlovskaja.pharmacy.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

public class MedicineInOrder implements Serializable {
    private static final long serialVersionUID = -5073755366792754335L;
    private int id;
    private String medicine;
    private int indivisible_amount;
    private String dosage;
    private boolean recipe_required;
    private int quantity;
    private int priceForOne;
    private int fk_order;

    // local variables, don't save in database
    private int amount; // field for Cart to set max available amount for buy
    private int rubForOne;    // currency unit
    private int coinForOne;   // currency unit
    private int priceForQuantity;
    private int rubForQuantity;    // currency unit
    private int coinForQuantity;   // currency unit


    public MedicineInOrder(int id) {
        this.id = id;
    }


    public MedicineInOrder(int id, String medicine, int indivisible_amount, String dosage, boolean recipe_required, int quantity, int priceForOne, int fk_order) {
        this.id = id;
        this.medicine = medicine;
        this.indivisible_amount = indivisible_amount;
        this.dosage = dosage;
        this.recipe_required = recipe_required;
        this.quantity = quantity;
        this.priceForOne = priceForOne;
        this.fk_order = fk_order;
    }

    public void setRubCoin() {
        this.priceForQuantity = priceForOne * quantity;
        this.rubForOne = priceForOne / 100;
        this.coinForOne = priceForOne % 100;
        this.rubForQuantity = priceForQuantity / 100;
        this.coinForQuantity = priceForQuantity % 100;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public void setPriceForOne(int priceForOne) {
        this.priceForOne = priceForOne;
    }

    public boolean isRecipe_required() {
        return recipe_required;
    }

    public void setRecipe_required(boolean recipe_required) {
        this.recipe_required = recipe_required;
    }

    public int getId() {
        return id;
    }

    public String getMedicine() {
        return medicine;
    }

    public String getDosage() {
        return dosage;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPriceForOne() {
        return priceForOne;
    }

    public int getIndivisible_amount() {
        return indivisible_amount;
    }

    public int getFk_order() {
        return fk_order;
    }

    public int getAmount() {
        return amount;
    }

    public int getRubForOne() {
        return rubForOne;
    }

    public int getCoinForOne() {
        return coinForOne;
    }

    public int getPriceForQuantity() {
        return priceForQuantity;
    }

    public int getRubForQuantity() {
        return rubForQuantity;
    }

    public int getCoinForQuantity() {
        return coinForQuantity;
    }

    public void setRubForOne(int rubForOne) {
        this.rubForOne = rubForOne;
    }

    public void setCoinForOne(int coinForOne) {
        this.coinForOne = coinForOne;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setPriceForQuantity(int priceForQuantity) {
        this.priceForQuantity = priceForQuantity;
    }

    public void setRubForQuantity(int rubForQuantity) {
        this.rubForQuantity = rubForQuantity;
    }

    public void setCoinForQuantity(int coinForQuantity) {
        this.coinForQuantity = coinForQuantity;
    }

    @Override
    public String toString() {
        return "MedicineInOrder{" +
                "id=" + id +
                ", medicine='" + medicine + '\'' +
                ", dosage='" + dosage + '\'' +
                ", quantity=" + quantity +
                ", price=" + priceForOne +
                ", fk_order=" + fk_order +
                '}' + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicineInOrder that = (MedicineInOrder) o;
        return indivisible_amount == that.indivisible_amount &&
                priceForOne == that.priceForOne &&
                fk_order == that.fk_order &&
                medicine.equals(that.medicine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(medicine, indivisible_amount, priceForOne, fk_order);
    }

    public static class NameComparator implements Comparator<MedicineInOrder> {
        public int compare(MedicineInOrder a, MedicineInOrder b) {
            return a.getMedicine().compareTo(b.getMedicine());
        }
    }

    public static class DosageComparator implements Comparator<MedicineInOrder> {
        public int compare(MedicineInOrder a, MedicineInOrder b) {
            return a.getDosage().compareTo(b.getDosage());
        }
    }

    public static class PriceComparator implements Comparator<MedicineInOrder> {
        public int compare(MedicineInOrder a, MedicineInOrder b) {
            return a.getPriceForOne() - b.getPriceForOne();
        }
    }
}
