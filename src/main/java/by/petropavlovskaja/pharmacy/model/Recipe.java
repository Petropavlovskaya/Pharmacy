package by.petropavlovskaja.pharmacy.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

/**
 * Class for recipe entity. Has next properties:
 * <b>serialVersionUID</b>, <b>id</b>, <b>medicine</b>, <b>dosage</b>, <b>doctorID</b>, <b>fkCustomer</b>,
 * <b>idMedicineInOrder</b>, <b>validity</b>, <b>needExtension</b> and <b>customerFio</b>
 */
public class Recipe implements Serializable {
    /**
     * Property - serial version UID
     */
    private static final long serialVersionUID = 5641772425449755744L;
    /**
     * Property - recipe ID
     */
    private int id;
    /**
     * Property - medicine name
     */
    private String medicine;
    /**
     * Property - medicine dosage
     */
    private String dosage;
    /**
     * Property - doctor ID
     */
    private int doctorID;
    /**
     * Property - customer foreign key
     */
    private int fkCustomer;
    /**
     * Property - ID medicine in order
     */
    private int idMedicineInOrder;
    /**
     * Property - recipe validity date
     */
    private Date validity;
    /**
     * Property - recipe need extension
     */
    private boolean needExtension;
    /**
     * Property - customer FIO
     */
    private String customerFio;

    /**
     * Create entity of class {@link Recipe#Recipe(int, String, String, int, int, int, Date, boolean)}
     *
     * @param id - recipe ID
     */
    public Recipe(int id) {
        this.id = id;
    }

    /**
     * Create entity of class {@link Recipe#Recipe(int)}
     *
     * @param id                - recipe ID
     * @param medicine          - medicine name
     * @param dosage            - medicine dosage
     * @param validity          - medicine validity date
     * @param fkCustomer        - foreign key for customer
     * @param idMedicineInOrder - ID purchased medicine by the recipe
     * @param doctorID          - ID doctor that wrote the recipe
     * @param needExtension     - is recipe need to extension
     */
    public Recipe(int id, String medicine, String dosage, int doctorID, int fkCustomer, int idMedicineInOrder,
                  Date validity, boolean needExtension) {
        this.id = id;
        this.medicine = medicine;
        this.dosage = dosage;
        this.doctorID = doctorID;
        this.fkCustomer = fkCustomer;
        this.idMedicineInOrder = idMedicineInOrder;
        this.validity = validity;
        this.needExtension = needExtension;
    }

    /**
     * The method of getting the customer FIO field value
     *
     * @return - a customer FIO value
     */
    public String getCustomerFio() {
        return customerFio;
    }

    /**
     * The method for setting the customer FIO field
     *
     * @param customerFio - a customer FIO
     */
    public void setCustomerFio(String customerFio) {
        this.customerFio = customerFio;
    }

    /**
     * The method of getting the recipe ID field value
     *
     * @return - a recipe ID value
     */
    public int getId() {
        return id;
    }

    /**
     * The method for setting the recipe ID field
     *
     * @param id - a recipe ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * The method of getting the medicine name field value
     *
     * @return - a medicine name value
     */
    public String getMedicine() {
        return medicine;
    }

    /**
     * The method for setting the medicine name field
     *
     * @param medicine - a medicine name
     */
    public void setMedicine(String medicine) {
        this.medicine = medicine;
    }

    /**
     * The method of getting the medicine dosage field value
     *
     * @return - a medicine dosage value
     */
    public String getDosage() {
        return dosage;
    }

    /**
     * The method for setting the medicine dosage field
     *
     * @param dosage - a medicine dosage
     */
    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    /**
     * The method of getting the doctor ID field value
     *
     * @return -  a doctor ID value
     */
    public int getDoctorID() {
        return doctorID;
    }

    /**
     * The method for setting the doctor ID field
     *
     * @param doctorID -  a doctor ID
     */
    public void setDoctorID(int doctorID) {
        this.doctorID = doctorID;
    }

    /**
     * The method of getting the customer's foreign key field value
     *
     * @return - a customer's foreign key value
     */
    public int getFkCustomer() {
        return fkCustomer;
    }

    /**
     * The method for setting the customer's foreign key field
     *
     * @param fkCustomer - a customer's foreign key
     */
    public void setFkCustomer(int fkCustomer) {
        this.fkCustomer = fkCustomer;
    }

    /**
     * The method of getting the ID medicine in order field value
     *
     * @return - an ID medicine in order value
     */
    public int getIdMedicineInOrder() {
        return idMedicineInOrder;
    }

    /**
     * The method for setting the ID medicine in order field
     *
     * @param idMedicineInOrder - an ID medicine in order
     */
    public void setIdMedicineInOrder(int idMedicineInOrder) {
        this.idMedicineInOrder = idMedicineInOrder;
    }

    /**
     * The method of getting the day of recipe validity field value
     *
     * @return - a day of recipe validity value
     */
    public Date getValidity() {
        return validity;
    }

    /**
     * The method for setting the day of recipe validity field
     *
     * @param validity - a day of recipe validity
     */
    public void setValidity(Date validity) {
        this.validity = validity;
    }

    /**
     * The method of getting is the recipe need an extension field value
     *
     * @return - is a recipe need an extension value
     */
    public boolean isNeedExtension() {
        return needExtension;
    }

    /**
     * The method for setting is the recipe need an extension field
     *
     * @param needExtension - is a recipe need an extension
     */
    public void setNeedExtension(boolean needExtension) {
        this.needExtension = needExtension;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", medicine='" + medicine + '\'' +
                ", dosage='" + dosage + '\'' +
                ", doctorID=" + doctorID +
                ", fkCustomer=" + fkCustomer +
                ", idMedicine_in_order=" + idMedicineInOrder +
                ", validity=" + validity +
                ", needExtension=" + needExtension +
                ", customerFio=" + customerFio + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return doctorID == recipe.doctorID &&
                fkCustomer == recipe.fkCustomer &&
                medicine.equals(recipe.medicine) &&
                validity.equals(recipe.validity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(medicine, doctorID, fkCustomer, validity);
    }

    /**
     * The nested class for compare recipe entity {@link RecipeNameComparator}, {@link RecipeDosageComparator},
     * {@link RecipeOrderIdComparator}
     */
    public static class RecipeCustomerComparator implements Comparator<Recipe> {

        /**
         * The method compare recipe by customer FIO
         *
         * @param a - one recipe
         * @param b - another recipe
         * @return - difference between two recipes
         */
        public int compare(Recipe a, Recipe b) {
            return a.getCustomerFio().compareTo(b.getCustomerFio());
        }
    }

    /**
     * The nested class for compare recipe entity {@link RecipeCustomerComparator}, {@link RecipeDosageComparator},
     * {@link RecipeOrderIdComparator}
     */
    public static class RecipeNameComparator implements Comparator<Recipe> {

        /**
         * The method compare recipe by medicine name
         *
         * @param a - one recipe
         * @param b - another recipe
         * @return - difference between two recipes
         */
        public int compare(Recipe a, Recipe b) {
            return a.getMedicine().compareTo(b.getMedicine());
        }
    }

    /**
     * The nested class for compare recipe entity {@link RecipeCustomerComparator}, {@link RecipeNameComparator},
     * {@link RecipeOrderIdComparator}
     */
    public static class RecipeDosageComparator implements Comparator<Recipe> {

        /**
         * The method compare recipe by medicine dosage
         *
         * @param a - one recipe
         * @param b - another recipe
         * @return - difference between two recipes
         */
        public int compare(Recipe a, Recipe b) {
            return a.getDosage().compareTo(b.getDosage());
        }
    }

    /**
     * The nested class for compare recipe entity {@link RecipeCustomerComparator}, {@link RecipeNameComparator},
     * {@link RecipeDosageComparator}
     */
    public static class RecipeOrderIdComparator implements Comparator<Recipe> {

        /**
         * The method compare recipe by ID medicine in order
         *
         * @param a - one recipe
         * @param b - another recipe
         * @return - difference between two recipes
         */
        public int compare(Recipe a, Recipe b) {
            return a.getIdMedicineInOrder() - b.getIdMedicineInOrder();
        }
    }
}
