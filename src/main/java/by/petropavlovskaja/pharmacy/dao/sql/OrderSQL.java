package by.petropavlovskaja.pharmacy.dao.sql;

/**
 * Enumeration for order SQL query
 */
public enum OrderSQL {
    GET_ALL_ORDERS_WITH_DETAILS("SELECT o.*, m.* FROM \"order\" o INNER JOIN medicine_in_order m on o.order_id = m.fk_order " +
            "WHERE fk_customer=? AND cart=false;"),
    FIND_ORDERS_WITH_DETAILS_BY_CUSTOMER_ID("SELECT o.*, m.* FROM \"order\" o INNER JOIN medicine_in_order m " +
            "on o.order_id = m.fk_order WHERE fk_customer=?;"),
    FIND_CART_BY_CUSTOMER_ID("SELECT * FROM \"order\" WHERE fk_customer = ? AND cart=true;"),
    INSERT_ORDER("INSERT INTO \"order\" (fk_customer, order_price, order_date, cart) " +
            "VALUES (?, ?, ?, false);"),
    INSERT_CART("INSERT INTO \"order\" (fk_customer, order_price, order_date, cart) VALUES (?, 0, '2020-01-01', true);"),
    INSERT_ACTIVE_MEDICINE_IN_CART("INSERT INTO active_med_in_cart (id_medicine, id_medicine_in_order) VALUES (?, ?);"),
    UPDATE_CART("UPDATE \"order\" SET order_price=? WHERE cart=true AND order_id=?;"),
    DELETE_ORDER("DELETE FROM \"order\" WHERE order_id=?;");

    /**
     * Property - query
     */
    private String query;

    /**
     * Constructor - create order SQL query
     *
     * @param query - query
     */
    OrderSQL(String query) {
        this.query = query;
    }

    /**
     * The method of getting an order SQL query {@link OrderSQL#query}
     *
     * @return - SQL query
     */
    public String getQuery() {
        return query;
    }
}



