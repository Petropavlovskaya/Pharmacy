package by.petropavlovskaja.pharmacy.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

public class Medicine implements Serializable {
    private static final long serialVersionUID = -8167764826188030073L;
    private int id;
    private String name;
    private int indivisible_amount;
    private int amount;
    private String dosage;
    private Date exp_date;
    private boolean recipe_required;
    private int price;
    private int rub; // currency unit
    private int coin; // currency unit
    private int added_by;
    private String pharm_form;
    private boolean customerNeedRecipe;

    public Medicine(int id) {
        this.id = id;
    }

    // For Doctor (list available medicine)
    public Medicine(String name, String dosage) {
        this.name = name;
        this.dosage = dosage;
    }

    public Medicine(int id, String name, int indivisible_amount, int amount, String dosage, Date exp_date,
                    boolean recipe_required, int price, int added_by, String pharm_form) {
        this.id = id;
        this.name = name;
        this.indivisible_amount = indivisible_amount;
        this.amount = amount;
        this.dosage = dosage;
        this.exp_date = exp_date;
        this.recipe_required = recipe_required;
        this.price = price;
        this.added_by = added_by;
        this.pharm_form = pharm_form;
        this.rub = price/100;
        this.coin = price%100;
    }

    public boolean isCustomerNeedRecipe() {
        return customerNeedRecipe;
    }

    public void setCustomerNeedRecipe(boolean customerNeedRecipe) {
        this.customerNeedRecipe = customerNeedRecipe;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getIndivisible_amount() {
        return indivisible_amount;
    }

    public int getAmount() {
        return amount;
    }

    public String getDosage() {
        return dosage;
    }

    public Date getExp_date() {
        return exp_date;
    }

    public boolean isRecipe_required() {
        return recipe_required;
    }

    public int getPrice() {
        return price;
    }

    public int getAdded_by() {
        return added_by;
    }

    public String getPharm_form() {
        return pharm_form;
    }

    public int getRub() {
        return rub;
    }

    public int getCoin() {
        return coin;
    }

    @Override
    public String toString() {
        return "Medicine{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", indivisible_amount=" + indivisible_amount +
                ", amount=" + amount +
                ", dosage='" + dosage + '\'' +
                ", exp_date=" + exp_date +
                ", recipe_required=" + recipe_required +
                ", price=" + price +
                ", added_by=" + added_by +
                ", pharm_form='" + pharm_form + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medicine medicine = (Medicine) o;
        return indivisible_amount == medicine.indivisible_amount &&
                name.equals(medicine.name) &&
                Objects.equals(dosage, medicine.dosage) &&
                exp_date.equals(medicine.exp_date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, dosage, price);
    }

    public static class MedicineNameComparator implements Comparator<Medicine> {
        public int compare(Medicine a, Medicine b) {
            return a.getName().compareTo(b.getName());
        }
    }
    public static class MedicineDosageComparator implements Comparator<Medicine> {
        public int compare(Medicine a, Medicine b) {
            return a.getDosage().compareTo(b.getDosage());
        }
    }
    public static class MedicineDateComparator implements Comparator<Medicine> {
        public int compare(Medicine a, Medicine b) {
            return a.getExp_date().compareTo(b.getExp_date());
        }
    }
}
