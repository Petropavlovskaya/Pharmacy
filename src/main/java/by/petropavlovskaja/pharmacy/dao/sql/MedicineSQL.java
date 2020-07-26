package by.petropavlovskaja.pharmacy.dao.sql;

/**
 * Enumeration for medicine SQL query
 */
public enum MedicineSQL {
    GET_ALL_MEDICINES("SELECT * FROM medicine ORDER BY \"name\", dosage, date(exp_date) ASC;"),
    GET_ALL_FOR_PAGE("SELECT * FROM medicine ORDER BY \"name\", dosage, date(exp_date) ASC  OFFSET ? ROWS FETCH FIRST ? ROWS ONLY;"),
    GET_ALL_RECIPE_MEDICINES("SELECT * FROM medicine WHERE recipe_required=true;"),
    GET_COUNT_MEDICINES("SELECT COUNT(*) FROM medicine;"),
    FIND_MEDICINES_BY_NAME_DOSAGE("SELECT * FROM medicine WHERE \"name\" = ? AND dosage = ?;"),
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

    /**
     * Property - query
     */
    private String query;

    /**
     * Constructor - create medicine SQL query
     *
     * @param query - query
     */
    MedicineSQL(String query) {
        this.query = query;
    }

    /**
     * The method of getting a medicine SQL query {@link MedicineSQL#query}
     *
     * @return - SQL query
     */
    public String getQuery() {
        return query;
    }
}
