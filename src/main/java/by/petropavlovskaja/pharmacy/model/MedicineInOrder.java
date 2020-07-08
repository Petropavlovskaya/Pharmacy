package by.petropavlovskaja.pharmacy.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

/** Class for medicine in order entity. Has next properties:
 * <b>serialVersionUID</b>, <b>id</b>, <b>medicine</b>, <b>indivisibleAmount</b>, <b>dosage</b>,
 * <b>recipeRequired</b>, <b>quantity</b>, <b>priceForOne</b>, <b>fkOrder</b>, <b>amount</b>,
 * <b>rubForOne</b>, <b>coinForOne</b>, <b>priceForQuantity</b>, <b>rubForQuantity</b> and <b>coinForQuantity</b>
 */
public class MedicineInOrder implements Serializable {
    /** Property - serial version UID */
    private static final long serialVersionUID = -5073755366792754335L;
    /** Property - medicine in order ID */
    private int id;
    /** Property - medicine name */
    private String medicine;
    /** Property - medicine indivisible amount */
    private int indivisibleAmount;
    /** Property - medicine dosage */
    private String dosage;
    /** Property - is medicine recipe required */
    private boolean recipeRequired;
    /** Property - medicine quantity in order */
    private int quantity;
    /** Property - price for one indivisible amount of medicine */
    private int priceForOne;
    /** Property - foreign key order by medicine */
    private int fkOrder;

    // local variables, don't save in database
    /** Property - available amount of medicine */
    private int amount; // field for Cart to set max available amount for buy
    /** Property - rub part of price for one indivisible amount of medicine */
    private int rubForOne;    // currency unit
    /** Property - coin part of price for one indivisible amount of medicine */
    private int coinForOne;   // currency unit
    /** Property - price for ordered amount of medicine */
    private int priceForQuantity;
    /** Property - rub part of price for ordered amount of medicine */
    private int rubForQuantity;    // currency unit
    /** Property - coin part of price for ordered amount of medicine */
    private int coinForQuantity;   // currency unit

    /** Create entity of class {@link MedicineInOrder#MedicineInOrder(int, String, int, String, boolean, int, int, int)}
     * @param id - medicine in order ID
     */
    public MedicineInOrder(int id) {
        this.id = id;
    }

    /** Create entity of class {@link MedicineInOrder#MedicineInOrder(int)}
     * @param id - medicine in order ID
     * @param medicine - medicine name
     * @param indivisibleAmount - medicine indivisible amount
     * @param dosage - medicine dosage
     * @param recipeRequired - is medicine recipe required
     * @param quantity - medicine quantity in order
     * @param priceForOne - price for one indivisible amount of medicine
     * @param fkOrder - foreign key order by medicine
     */
    public MedicineInOrder(int id, String medicine, int indivisibleAmount, String dosage, boolean recipeRequired, int quantity, int priceForOne, int fkOrder) {
        this.id = id;
        this.medicine = medicine;
        this.indivisibleAmount = indivisibleAmount;
        this.dosage = dosage;
        this.recipeRequired = recipeRequired;
        this.quantity = quantity;
        this.priceForOne = priceForOne;
        this.fkOrder = fkOrder;
    }

    /**
     * The method for setting next fields:
     * {@link MedicineInOrder#priceForQuantity}, {@link MedicineInOrder#rubForOne}, {@link MedicineInOrder#coinForOne},
     * {@link MedicineInOrder#rubForQuantity} and {@link MedicineInOrder#coinForQuantity}.
     * Fields are used in the view layer
     */
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

    /**
     * The method for setting the price for one field
     *
     * @param priceForOne - the price for one medicine
     */
    public void setPriceForOne(int priceForOne) {
        this.priceForOne = priceForOne;
    }

    /** The method of getting is the medicine required a recipe
     * @return - is the medicine required a recipe
     */
    public boolean isRecipeRequired() {
        return recipeRequired;
    }

//    public void setRecipeRequired(boolean recipeRequired) {
//        this.recipeRequired = recipeRequired;
//    }

    /** The method of getting the medicine in order ID value
     * @return - a medicine in order ID value
     */
    public int getId() {
        return id;
    }

    /** The method of getting the medicine name value
     * @return - a medicine name value
     */
    public String getMedicine() {
        return medicine;
    }

    /** The method of getting the medicine dosage value
     * @return - a medicine dosage value
     */
    public String getDosage() {
        return dosage;
    }

    /** The method of getting the medicine quantity value
     * @return - a medicine quantity value
     */
    public int getQuantity() {
        return quantity;
    }

    /** The method of getting the medicine price for one value
     * @return - a medicine price for one value
     */
    public int getPriceForOne() {
        return priceForOne;
    }

    /** The method of getting the medicine indivisible amount value
     * @return - a medicine indivisible amount value
     */
    public int getIndivisibleAmount() {
        return indivisibleAmount;
    }

    /** The method of getting the foreign key value
     * @return - a medicine foreign key value
     */
    public int getFkOrder() {
        return fkOrder;
    }

    /** The method of getting the medicine amount value
     * @return - a medicine amount value
     */
    public int getAmount() {
        return amount;
    }

    /** The method of getting the medicine rubs for one value
     * @return - a medicine rubs for one value
     */
    public int getRubForOne() {
        return rubForOne;
    }

    /** The method of getting the medicine coins for one value
     * @return - a medicine coins for one value
     */
    public int getCoinForOne() {
        return coinForOne;
    }

    /** The method of getting the medicine price for quantity value
     * @return - a medicine price for quantity value
     */
    public int getPriceForQuantity() {
        return priceForQuantity;
    }

    /** The method of getting the medicine rubs for quantity value
     * @return - a medicine rubs for quantity value
     */
    public int getRubForQuantity() {
        return rubForQuantity;
    }

    /** The method of getting the medicine coins for quantity value
     * @return - a medicine coins for quantity value
     */
    public int getCoinForQuantity() {
        return coinForQuantity;
    }

    /**
     * The method for setting the medicine quantity
     *
     * @param quantity - the medicine quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * The method for setting the medicine available amount
     *
     * @param amount - the medicine available amount
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

//    public void setPriceForQuantity(int priceForQuantity) {
//        this.priceForQuantity = priceForQuantity;
//    }

//    public void setRubForQuantity(int rubForQuantity) {
//        this.rubForQuantity = rubForQuantity;
//    }

//    public void setCoinForQuantity(int coinForQuantity) {
//        this.coinForQuantity = coinForQuantity;
//    }

    @Override
    public String toString() {
        return "MedicineInOrder{" +
                "id=" + id +
                ", medicine='" + medicine + '\'' +
                ", dosage='" + dosage + '\'' +
                ", quantity=" + quantity +
                ", price=" + priceForOne +
                ", fk_order=" + fkOrder +
                '}' + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicineInOrder that = (MedicineInOrder) o;
        return indivisibleAmount == that.indivisibleAmount &&
                priceForOne == that.priceForOne &&
                fkOrder == that.fkOrder &&
                medicine.equals(that.medicine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(medicine, indivisibleAmount, priceForOne, fkOrder);
    }

    /** The nested class for compare medicine in order entity {@link MedicineInOrder.DosageComparator}, {@link MedicineInOrder.PriceComparator}  */
    public static class NameComparator implements Comparator<MedicineInOrder> {

        /**
         * The method compare medicine in order by medicine name
         *
         * @param a - one medicine in order
         * @param b - another medicine in order
         * @return - difference between two medicines in order
         */
        public int compare(MedicineInOrder a, MedicineInOrder b) {
            return a.getMedicine().compareTo(b.getMedicine());
        }
    }

    /** The nested class for compare medicine in order entity {@link MedicineInOrder.NameComparator}, {@link MedicineInOrder.PriceComparator}  */
    public static class DosageComparator implements Comparator<MedicineInOrder> {

        /**
         * The method compare medicine in order by medicine dosage
         *
         * @param a - one medicine in order
         * @param b - another medicine in order
         * @return - difference between two medicines in order
         */
        public int compare(MedicineInOrder a, MedicineInOrder b) {
            return a.getDosage().compareTo(b.getDosage());
        }
    }

    /** The nested class for compare medicine in order entity {@link MedicineInOrder.NameComparator}, {@link MedicineInOrder.DosageComparator}  */
    public static class PriceComparator implements Comparator<MedicineInOrder> {

        /**
         * The method compare medicine in order by medicine price
         *
         * @param a - one medicine in order
         * @param b - another medicine in order
         * @return - difference between two medicines in order
         */
        public int compare(MedicineInOrder a, MedicineInOrder b) {
            return a.getPriceForOne() - b.getPriceForOne();
        }
    }
}
