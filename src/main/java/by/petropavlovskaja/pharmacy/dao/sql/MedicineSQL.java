package by.petropavlovskaja.pharmacy.dao.sql;

public enum MedicineSQL {
    GET_ALL_MEDICINES("SELECT * FROM medicine WHERE recipe_required=true"),
    FIND_MEDICINES_BY_NAME("SELECT * FROM medicine WHERE \"name\" = ?;"),
    FIND_MEDICINES_BY_ID("SELECT * FROM medicine WHERE id = ?;"),
    INSERT_MEDICINE("INSERT INTO medicine (name, indivisible_amount, amount, dosage, pharm_form, exp_date, recipe_required, price, added_by) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);"),
    UPDATE_MEDICINE("UPDATE medicine SET name=?, indivisible_amount=?, amount=?, dosage=?, pharm_form=?, exp_date=?, recipe_required=?, price=?, added_by=? " +
            "WHERE id = ?;"),
    DELETE_MEDICINE("DELETE from medicine WHERE id = ?;");


    private String query;

    MedicineSQL(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
