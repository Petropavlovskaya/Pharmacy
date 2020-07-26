package by.petropavlovskaja.pharmacy.dao.sql;

/**
 * Enumeration for recipe SQL query
 */
public enum RecipeSQL {
    GET_ALL_RECIPE_BY_CUSTOMER_ID("SELECT * FROM recipe WHERE fk_customer=?;"),
    GET_ALL_ACTIVE_RECIPE_BY_CUSTOMER_ID("SELECT * FROM recipe WHERE id_medicine_in_order is null AND " +
            "validity > current_date AND fk_customer = ?;"),
    GET_ALL_ORDERED_RECIPE("SELECT r.*, a.surname, a.\"name\", a.patronymic FROM recipe r " +
            "INNER JOIN account a ON r.fk_customer=a.id  WHERE r.need_extension=true AND id_medicine_in_order is null;"),
    UPDATE_NEED_EXTENSION_BY_RECIPE_ID("UPDATE recipe SET need_extension=true, validity=null WHERE recipe_id=?;"),
    UPDATE_REFUSE_RECIPE_BY_ID("UPDATE recipe SET id_medicine_in_order=-1, doctor_id=? WHERE recipe_id=?;"),
    UPDATE_ID_MEDICINE_IN_ORDER("UPDATE recipe SET id_medicine_in_order=? WHERE recipe_id=?;"),
    VALIDATE_RECIPE("UPDATE recipe SET need_extension=false, validity=?, doctor_id=? WHERE recipe_id=?;"),
    INSERT_RECIPE_CUSTOMER("INSERT INTO recipe (medicine, dosage, fk_customer, need_extension) " +
            "VALUES (?, ?, ?, true);"),
    INSERT_RECIPE_DOCTOR("INSERT INTO recipe (medicine, dosage, doctor_id, fk_customer, validity, need_extension) " +
            "VALUES (?, ?, ?, ?, ?, false);"),
    DELETE_RECIPE_BY_ID("DELETE FROM recipe WHERE recipe_id=?;");

    /**
     * Property - query
     */
    private String query;

    /**
     * Constructor - create recipe SQL query
     *
     * @param query - query
     */
    RecipeSQL(String query) {
        this.query = query;
    }

    /**
     * The method of getting a recipe SQL query {@link RecipeSQL#query}
     *
     * @return - SQL query
     */
    public String getQuery() {
        return query;
    }
}
