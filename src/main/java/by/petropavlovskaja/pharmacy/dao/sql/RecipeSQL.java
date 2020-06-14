package by.petropavlovskaja.pharmacy.dao.sql;

public enum RecipeSQL {
    GET_ALL_RECIPE_BY_CUSTOMER_ID("SELECT * FROM recipe WHERE fk_customer=?;"),
    GET_ALL_ACTIVE_RECIPE_BY_CUSTOMER_ID("SELECT * FROM recipe WHERE id_medicine_in_order is null AND " +
            "validity > current_date AND fk_customer = ?;"),
    GET_ALL_USED_RECIPE_BY_CUSTOMER_ID("SELECT * FROM recipe WHERE fk_customer=? AND id_medicine_in_order IS NOT null;"),
    GET_ALL_ORDERED_RECIPE("SELECT r.*, a.surname, a.\"name\", a.patronymic FROM recipe r " +
            "INNER JOIN account a ON r.fk_customer=a.id  WHERE r.need_extension =true;"),
    UPDATE_NEED_EXTENSION_BY_RECIPE_ID("UPDATE recipe SET need_extension=true, validity=null WHERE recipe_id=?;"),
    VALIDATE_RECIPE("UPDATE recipe SET need_extension=false, validity=?, doctor_id=? WHERE recipe_id=?;"),
    INSERT_RECIPE_CUSTOMER("INSERT INTO recipe (medicine, dosage, fk_customer, need_extension) " +
            "VALUES (?, ?, ?, true);"),
    INSERT_RECIPE_DOCTOR("INSERT INTO recipe (medicine, dosage, doctor_id, fk_customer, validity, need_extension) " +
            "VALUES (?, ?, ?, ?, ?, false);"),
    DELETE_RECIPE_BY_ID("DELETE FROM recipe WHERE recipe_id=?;")


/*    //    GET_ALL_MEDICINES_IN_ORDER_BY_ORDER_ID("SELECT * FROM medicine_in_order WHERE fk_order=?;"),
    FIND_ORDERS_WITH_DETAILS_BY_CUSTOMER_ID("SELECT o.*, m.* FROM \"order\" o INNER JOIN medicine_in_order m " +
            "on o.order_id = m.fk_order WHERE fk_customer=?;"),
    FIND_CART_BY_CUSTOMER_ID("SELECT * FROM \"order\" WHERE fk_customer = ? AND cart=true;"),
    *//*    FIND_CART_WITH_DETAILS_BY_CUSTOMER_ID("SELECT o.*, m.* FROM \"order\" o " +
                "LEFT JOIN medicine_in_order m on o.id = m.fk_order WHERE fk_customer=? AND cart=true;"),*//*
    FIND_ACTUAL_PRICE_FOR_MEDICINES_IN_CART_BY_CART_ID("SELECT o.id, o.medicine, o.indivisible_amount, o.dosage, " +
            "o.quantity, m.price, o.fk_order, m.amount " +
            "FROM medicine_in_order o LEFT JOIN active_med_in_cart a ON o.id = a.id_medicine_in_order " +
            "LEFT JOIN medicine m ON a.id_medicine = m.id WHERE fk_order=?;"),
    FIND_MEDICINE_IN_CART_BY_NAME_DOSAGE_AMOUNT("SELECT * FROM medicine_in_order WHERE medicine=? AND dosage=? AND indivisible_amount=? AND fk_order=?;"),
    //    FIND_ORDER_BY_CUSTOMER_ID("SELECT * FROM \"order\" WHERE fk_customer = ?;"),
    FIND_ORDER_WITH_DETAILS_BY_DATE_RANGE("SELECT o.*, m.* FROM \"order\" o INNER JOIN medicine_in_order m " +
            "on o.id = m.fk_order WHERE o.fk_customer = ? and (o.order_date between ? and ?;"),

    INSERT_ORDER("INSERT INTO \"order\" (fk_customer, payment_state, order_price, order_date, cart) " +
            "VALUES (?, ?, ?, ?, ?);"),
    INSERT_CART("INSERT INTO \"order\" (fk_customer, order_price, order_date, cart) VALUES (?, 0, '2020-01-01', true);"),
    INSERT_ACTIVE_MEDICINE_IN_CART("INSERT INTO active_med_in_cart (id_medicine, id_medicine_in_order) VALUES (?, ?);"),
    INSERT_MEDICINE_IN_ORDER("INSERT INTO medicine_in_order (medicine, dosage, indivisible_amount, quantity, price, fk_order) " +
            "VALUES (?, ?, ?, ?, ?, ?);"),

    //    UPDATE_ORDER("UPDATE \"order\" SET payment_state=?, order_price=? WHERE id = ? AND fk_customer=?;");
    UPDATE_CART("UPDATE \"order\" SET order_price=? WHERE cart=true AND id=?;"),
    UPDATE_MEDICINES_QUANTITY_IN_CART_BY_ID("UPDATE medicine_in_order SET quantity=? WHERE id=?;"),
    UPDATE_ALL_PRICES_IN_CART_BY_ID_AND_CART_ID("UPDATE medicine_in_order SET price=? WHERE id=? AND fk_order=?;"),
    */
    ;

    private String query;
    RecipeSQL(String query) {
        this.query = query;
    }
    public String getQuery() {
        return query;
    }
}
