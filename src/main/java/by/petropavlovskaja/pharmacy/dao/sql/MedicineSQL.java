package by.petropavlovskaja.pharmacy.dao.sql;

public enum MedicineSQL {
    GET_ALL_MEDICINES("SELECT * FROM medicine;"),
    GET_ALL_RECIPE_MEDICINES("SELECT * FROM medicine WHERE recipe_required=true;"),
    FIND_MEDICINES_BY_NAME("SELECT * FROM medicine WHERE \"name\" = ?;"),
    GET_INFO_FOR_ORDER_BY_ORDER_ID("SELECT med.id, med.\"name\", med.dosage, med.price, med.amount FROM medicine_in_order mio \n" +
            "right JOIN active_med_in_cart a ON mio.id = a.id_medicine_in_order \n" +
            "right JOIN medicine med ON a.id_medicine = med.id WHERE fk_order=? ;"),
    FIND_MEDICINES_BY_ID("SELECT * FROM medicine WHERE id = ?;"),
    INSERT_MEDICINE("INSERT INTO medicine (name, indivisible_amount, amount, dosage, pharm_form, exp_date, recipe_required, price, added_by) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);"),
    UPDATE_MEDICINE("UPDATE medicine SET name=?, indivisible_amount=?, amount=?, dosage=?, pharm_form=?, exp_date=?, recipe_required=?, price=?, added_by=? " +
            "WHERE id = ?;"),
    UPDATE_MEDICINE_AFTER_BUY("UPDATE medicine SET amount=? WHERE id = ?;"),
    DELETE_MEDICINE("DELETE from medicine WHERE id = ?;");


    private String query;

    MedicineSQL(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
