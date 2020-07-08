package by.petropavlovskaja.pharmacy.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

/** Class for medicine entity. Has next properties:
 * <b>serialVersionUID</b>, <b>id</b>, <b>name</b>, <b>indivisibleAmount</b>, <b>amount</b>, <b>dosage</b>,
 * <b>expDate</b>, <b>recipeRequired</b>, <b>price</b>, <b>rub</b>, <b>coin</b>,
 * <b>addedBy</b>, <b>pharmForm</b> and <b>customerNeedRecipe</b>
 */
public class Medicine implements Serializable {
    /** Property - serial version UID */
    private static final long serialVersionUID = -8167764826188030073L;
    /** Property - medicine ID */
    private int id;
    /** Property - medicine name */
    private String name;
    /** Property - medicine indivisible amount */
    private int indivisibleAmount;
    /** Property - medicine amount */
    private int amount;
    /** Property - medicine dosage */
    private String dosage;
    /** Property - medicine expiration date */
    private Date expDate;
    /** Property - is medicine recipe required */
    private boolean recipeRequired;
    /** Property - medicine price */
    private int price;
    /** Property - rub part of medicine price */
    private int rub; // currency unit
    /** Property - coin part of medicine price */
    private int coin; // currency unit
    /** Property - ID pharmacist who added medicine */
    private int addedBy;
    /** Property - medicine pharmacy form */
    private String pharmForm;
    /** Property - is customer need the recipe for this medicine */
    private boolean customerNeedRecipe;

    /** Create entity of class {@link Medicine#Medicine(String, String)}, {@link Medicine#Medicine(int, String, String, int, int)},
     * {@link Medicine#Medicine(int, String, int, int, String, Date, boolean, int, int, String)}
     * @param id - medicine ID
     */
    public Medicine(int id) {
        this.id = id;
    }

    /** Create entity of class {@link Medicine#Medicine(int)}, {@link Medicine#Medicine(int, String, String, int, int)},
     * {@link Medicine#Medicine(int, String, int, int, String, Date, boolean, int, int, String)}
     * @param name - medicine name
     * @param dosage - medicine dosage
     */
    // For Doctor (list available medicine)
    public Medicine(String name, String dosage) {
        this.name = name;
        this.dosage = dosage;
    }

    /** Create entity of class {@link Medicine#Medicine(int)}, {@link Medicine#Medicine(String, String)},
     * {@link Medicine#Medicine(int, String, int, int, String, Date, boolean, int, int, String)}
     * @param id - medicine ID
     * @param name - medicine name
     * @param dosage - medicine dosage
     * @param price - medicine price
     * @param amount - medicine amount
     */
    // For create Order (recount available amount)
    public Medicine(int id, String name, String dosage, int price, int amount) {
        this.id = id;
        this.name = name;
        this.dosage = dosage;
        this.price = price;
        this.amount = amount;
    }

    /** Create entity of class {@link Medicine#Medicine(int)}, {@link Medicine#Medicine(int, String, String, int, int)},
     * {@link Medicine#Medicine(String, String)}
     * @param id - medicine ID
     * @param name - medicine name
     * @param indivisibleAmount - medicine indivisible amount
     * @param amount - medicine amount
     * @param dosage - medicine dosage
     * @param expDate - medicine expiration day
     * @param recipeRequired - is recipe required for medicine
     * @param price - medicine price
     * @param addedBy - pharmacist ID who added medicine
     * @param pharmForm - medicine pharmacy form
     */
    public Medicine(int id, String name, int indivisibleAmount, int amount, String dosage, Date expDate,
                    boolean recipeRequired, int price, int addedBy, String pharmForm) {
        this.id = id;
        this.name = name;
        this.indivisibleAmount = indivisibleAmount;
        this.amount = amount;
        this.dosage = dosage;
        this.expDate = expDate;
        this.recipeRequired = recipeRequired;
        this.price = price;
        this.addedBy = addedBy;
        this.pharmForm = pharmForm;
        this.rub = price / 100;
        this.coin = price % 100;
    }

    /** The method of getting is the customer need a recipe for that medicine
     * @return - is the customer need a recipe for that medicine
     */
    public boolean isCustomerNeedRecipe() {
        return customerNeedRecipe;
    }

    /**
     * The method for setting is the customer need a recipe for that medicine
     *
     * @param customerNeedRecipe - is the customer need a recipe for that medicine
     */
    public void setCustomerNeedRecipe(boolean customerNeedRecipe) {
        this.customerNeedRecipe = customerNeedRecipe;
    }

    /** The method of getting the medicine ID value
     * @return - a medicine ID value
     */
    public int getId() {
        return id;
    }

    /** The method of getting the medicine name value
     * @return - a medicine name value
     */
    public String getName() {
        return name;
    }

    /** The method of getting the medicine indivisible amount value
     * @return - a medicine indivisible amount value
     */
    public int getIndivisibleAmount() {
        return indivisibleAmount;
    }

    /** The method of getting the medicine amount value
     * @return - a medicine amount value
     */
    public int getAmount() {
        return amount;
    }

    /** The method of getting the medicine dosage value
     * @return - a medicine dosage value
     */
    public String getDosage() {
        return dosage;
    }

    /** The method of getting the medicine expiration date value
     * @return - a medicine expiration date value
     */
    public Date getExpDate() {
        return expDate;
    }

    /** The method of getting is the medicine required a recipe
     * @return - is the medicine required a recipe
     */
    public boolean isRecipeRequired() {
        return recipeRequired;
    }

    /** The method of getting the medicine price value
     * @return - a medicine price value
     */
    public int getPrice() {
        return price;
    }

    /** The method of getting the pharmacist ID value who added a medicine
     * @return - a pharmacist ID value
     */
    public int getAddedBy() {
        return addedBy;
    }

    /** The method of getting the medicine pharmacy form value
     * @return - a medicine pharmacy form value
     */
    public String getPharmForm() {
        return pharmForm;
    }

    /** The method of getting the rub part of medicine price value
     * @return - a rub part of medicine price value
     */
    public int getRub() {
        return rub;
    }

    /** The method of getting the coin part of medicine price value
     * @return - a coin part of medicine price value
     */
    public int getCoin() {
        return coin;
    }

    /**
     * The method for setting amount of medicine value
     *
     * @param amount - an amount of medicine value
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Medicine{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", indivisible_amount=" + indivisibleAmount +
                ", amount=" + amount +
                ", dosage='" + dosage + '\'' +
                ", exp_date=" + expDate +
                ", recipe_required=" + recipeRequired +
                ", price=" + price +
                ", added_by=" + addedBy +
                ", pharm_form='" + pharmForm + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medicine medicine = (Medicine) o;
        return indivisibleAmount == medicine.indivisibleAmount &&
                name.equals(medicine.name) &&
                Objects.equals(dosage, medicine.dosage) &&
                expDate.equals(medicine.expDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, dosage, price);
    }

    /** The nested class for compare medicine entity {@link Medicine.MedicineDosageComparator}, {@link Medicine.MedicineDateComparator}  */
    public static class MedicineNameComparator implements Comparator<Medicine> {

        /**
         * The method compare medicine by medicine name
         *
         * @param a - one medicine
         * @param b - another medicine
         * @return - difference between two medicines
         */
        public int compare(Medicine a, Medicine b) {
            return a.getName().compareTo(b.getName());
        }
    }

    /** The nested class for compare medicine entity {@link Medicine.MedicineNameComparator}, {@link Medicine.MedicineDateComparator}  */
    public static class MedicineDosageComparator implements Comparator<Medicine> {

        /**
         * The method compare medicine by medicine dosage
         *
         * @param a - one medicine
         * @param b - another medicine
         * @return - difference between two medicines
         */
        public int compare(Medicine a, Medicine b) {
            return a.getDosage().compareTo(b.getDosage());
        }
    }

    /** The nested class for compare medicine entity {@link Medicine.MedicineDosageComparator}, {@link Medicine.MedicineNameComparator}  */
    public static class MedicineDateComparator implements Comparator<Medicine> {

        /**
         * The method compare medicine by medicine expiration date
         *
         * @param a - one medicine
         * @param b - another medicine
         * @return - difference between two medicines
         */
        public int compare(Medicine a, Medicine b) {
            return a.getExpDate().compareTo(b.getExpDate());
        }
    }
}
