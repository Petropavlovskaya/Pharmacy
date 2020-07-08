package by.petropavlovskaja.pharmacy.dao.sql;

/** Enumeration for order SQL query */
public enum OrderSQL {
    GET_ALL_ORDERS_WITH_DETAILS("SELECT o.*, m.* FROM \"order\" o INNER JOIN medicine_in_order m on o.order_id = m.fk_order " +
            "WHERE fk_customer=? AND cart=false;"),
    FIND_ORDERS_WITH_DETAILS_BY_CUSTOMER_ID("SELECT o.*, m.* FROM \"order\" o INNER JOIN medicine_in_order m " +
            "on o.order_id = m.fk_order WHERE fk_customer=?;"),
    FIND_CART_BY_CUSTOMER_ID("SELECT * FROM \"order\" WHERE fk_customer = ? AND cart=true;"),
    FIND_ACTUAL_PRICE_FOR_MEDICINES_IN_CART_BY_CART_ID("SELECT mio.id, mio.medicine, mio.indivisible_amount, mio.dosage, mio.recipe_required, \n" +
        "mio.quantity, med.price, mio.fk_order, med.amount \n" +
        "FROM medicine_in_order mio LEFT JOIN active_med_in_cart a ON mio.id = a.id_medicine_in_order \n" +
        "LEFT JOIN medicine med ON a.id_medicine = med.id WHERE fk_order=?;"),
    FIND_MEDICINE_IN_CART_BY_NAME_DOSAGE_AMOUNT("SELECT * FROM medicine_in_order WHERE medicine=? AND dosage=? AND indivisible_amount=? AND fk_order=?;"),
    FIND_MEDICINE_IN_CART_BY_ORDER_ID("SELECT * FROM medicine_in_order WHERE fk_order=?;"),
    INSERT_ORDER("INSERT INTO \"order\" (fk_customer, order_price, order_date, cart) " +
            "VALUES (?, ?, ?, false);"),
    INSERT_CART("INSERT INTO \"order\" (fk_customer, order_price, order_date, cart) VALUES (?, 0, '2020-01-01', true);"),
    INSERT_ACTIVE_MEDICINE_IN_CART("INSERT INTO active_med_in_cart (id_medicine, id_medicine_in_order) VALUES (?, ?);"),
    INSERT_MEDICINE_IN_ORDER("INSERT INTO medicine_in_order (medicine, dosage, recipe_required, indivisible_amount, quantity, price, fk_order) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?);"),
    UPDATE_CART("UPDATE \"order\" SET order_price=? WHERE cart=true AND order_id=?;"),
    UPDATE_MEDICINES_QUANTITY_IN_CART_BY_ID("UPDATE medicine_in_order SET quantity=? WHERE id=?;"),
    UPDATE_PRICE_AMOUNT_IN_CART_BY_ID_AND_CART_ID("UPDATE medicine_in_order SET price=?, quantity=? WHERE id=?;"),

    DELETE_MEDICINE_BY_ID("DELETE FROM medicine_in_order WHERE id=?;");

    /** Property - query */
    private String query;

    /** Constructor - create order SQL query
     * @param query - query
     */
    OrderSQL(String query) {
        this.query = query;
    }

    /** The method of getting an order SQL query {@link OrderSQL#query}
     * @return - SQL query
     */
    public String getQuery() {
        return query;
    }
}



