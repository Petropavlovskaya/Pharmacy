package by.petropavlovskaja.pharmacy.dao.sql;

/**
 * Enumeration for medicine in order SQL query
 */
public enum MedicineInOrderSQL {
    FIND_ACTUAL_PRICE_FOR_MEDICINES_IN_CART_BY_CART_ID("SELECT mio.id, mio.medicine, mio.indivisible_amount, mio.dosage, " +
            "mio.exp_date, mio.recipe_required, mio.quantity, med.price, mio.fk_order, med.amount, med.name " +
            "FROM medicine_in_order mio LEFT JOIN active_med_in_cart a ON mio.id = a.id_medicine_in_order " +
            "LEFT JOIN medicine med ON a.id_medicine = med.id WHERE fk_order=?;"),
    FIND_MEDICINE_IN_CART_BY_NAME_DOSAGE_AMOUNT("SELECT * FROM medicine_in_order WHERE medicine=? AND dosage=? AND indivisible_amount=? AND fk_order=?;"),
    FIND_MEDICINE_IN_CART_BY_ORDER_ID("SELECT * FROM medicine_in_order WHERE fk_order=?;"),
    INSERT_MEDICINE_IN_ORDER("INSERT INTO medicine_in_order (medicine, dosage, exp_date, recipe_required, indivisible_amount, quantity, price, fk_order) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?);"),
    UPDATE_PRICE_AMOUNT_IN_CART_BY_ID_AND_CART_ID("UPDATE medicine_in_order SET price=?, quantity=? WHERE id=?;"),
    UPDATE_MEDICINES_QUANTITY_IN_CART_BY_ID("UPDATE medicine_in_order SET quantity=? WHERE id=?;"),
    DELETE_MEDICINE_BY_ID("DELETE FROM medicine_in_order WHERE id=?;");

    /**
     * Property - query
     */
    private String query;

    /**
     * Constructor - create medicine in order SQL query
     *
     * @param query - query
     */
    MedicineInOrderSQL(String query) {
        this.query = query;
    }

    /**
     * The method of getting a medicine in order SQL query {@link MedicineInOrderSQL#query}
     *
     * @return - SQL query
     */
    public String getQuery() {
        return query;
    }
}
