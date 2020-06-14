package by.petropavlovskaja.pharmacy.model;

import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

public class Recipe /*implements Comparable<Recipe>*/ {
    private int id;
    private String medicine;
    private String dosage;
    private int doctor_id;
    private int fk_customer;
    private int id_medicine_in_order;
    private Date validity;
    private boolean need_extension;
    private String customerFio;

    public Recipe(int id) {
        this.id = id;
    }

    public Recipe(int id, String medicine, String dosage, int doctor_id, int fk_customer, int id_medicine_in_order,
                  Date validity, boolean need_extension) {
        this.id = id;
        this.medicine = medicine;
        this.dosage = dosage;
        this.doctor_id = doctor_id;
        this.fk_customer = fk_customer;
        this.id_medicine_in_order = id_medicine_in_order;
        this.validity = validity;
        this.need_extension = need_extension;
    }

    public String getCustomerFio() {
        return customerFio;
    }

    public void setCustomerFio(String customerFio) {
        this.customerFio = customerFio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMedicine() {
        return medicine;
    }

    public void setMedicine(String medicine) {
        this.medicine = medicine;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public int getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(int doctor_id) {
        this.doctor_id = doctor_id;
    }

    public int getFk_customer() {
        return fk_customer;
    }

    public void setFk_customer(int fk_customer) {
        this.fk_customer = fk_customer;
    }

    public int getId_medicine_in_order() {
        return id_medicine_in_order;
    }

    public void setId_medicine_in_order(int id_medicine_in_order) {
        this.id_medicine_in_order = id_medicine_in_order;
    }

    public Date getValidity() {
        return validity;
    }

    public void setValidity(Date validity) {
        this.validity = validity;
    }

    public boolean isNeed_extension() {
        return need_extension;
    }

    public void setNeed_extension(boolean need_extension) {
        this.need_extension = need_extension;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", medicine='" + medicine + '\'' +
                ", dosage='" + dosage + '\'' +
                ", doctor_id=" + doctor_id +
                ", fk_customer=" + fk_customer +
                ", id_medicine_in_order=" + id_medicine_in_order +
                ", validity=" + validity +
                ", need_extension=" + need_extension +
                ", customerFio='" + customerFio + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return doctor_id == recipe.doctor_id &&
                fk_customer == recipe.fk_customer &&
                medicine.equals(recipe.medicine) &&
                validity.equals(recipe.validity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(medicine, doctor_id, fk_customer, validity);
    }

/*    @Override
    public int compareTo(Recipe o) {
        return 0;
    }*/

    public static class RecipeNameComparator implements Comparator<Recipe> {
        public int compare(Recipe a, Recipe b) {
            return a.getMedicine().compareTo(b.getMedicine());
        }
    }
    public static class RecipeDosageComparator implements Comparator<Recipe> {
        public int compare(Recipe a, Recipe b) {
            return a.getDosage().compareTo(b.getDosage());
        }
    }
    public static class RecipeOrderIdComparator implements Comparator<Recipe> {
        public int compare(Recipe a, Recipe b) {
            return a.getId_medicine_in_order() - b.getId_medicine_in_order();
        }
    }
}
