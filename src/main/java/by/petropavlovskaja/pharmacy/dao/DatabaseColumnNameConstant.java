package by.petropavlovskaja.pharmacy.dao;

public class DatabaseColumnNameConstant {
    protected static final String ACCOUNT_SURNAME = "surname";
    protected static final String ACCOUNT_NAME = "name";
    protected static final String ACCOUNT_ID = "id";
    protected static final String ACCOUNT_PATRONYMIC = "patronymic";
    protected static final String ACCOUNT_PHONE = "phone";
    protected static final String ACCOUNT_BALANCE = "balance";
    protected static final String ACCOUNT_ROLE_NAME = "role_name";
    protected static final String ACCOUNT_STATUS = "status";

    protected static final String MEDICINE_DOSAGE = "dosage";
    protected static final String MEDICINE_NAME = "name";
    protected static final String MEDICINE_AMOUNT = "amount";
    protected static final String MEDICINE_ID = "id";
    protected static final String MEDICINE_PRICE = "price";
    protected static final String MEDICINE_INDIVISIBLE_AMOUNT = "indivisible_amount";
    protected static final String MEDICINE_EXP_DATE = "exp_date";
    protected static final String MEDICINE_RECIPE_REQUIRED = "recipe_required";
    protected static final String MEDICINE_ADDED_BY = "added_by";
    protected static final String MEDICINE_PHARM_FORM = "pharm_form";

    // MIO - medicine in order
    protected static final String MIO_ID = "id";
    protected static final String MIO_MEDICINE = "medicine";
    protected static final String MIO_INDIVISIBLE_AMOUNT = "indivisible_amount";
    protected static final String MIO_AMOUNT = "amount";
    protected static final String MIO_DOSAGE = "dosage";
    protected static final String MIO_EXP_DATE = "exp_date";
    protected static final String MIO_RECIPE_REQUIRED = "recipe_required";
    protected static final String MIO_QUANTITY = "quantity";
    protected static final String MIO_PRICE = "price";
    protected static final String MIO_ORDER_KEY = "fk_order";

    protected static final String ORDER_ID = "order_id";
    protected static final String ORDER_PRICE = "order_price";
    protected static final String ORDER_CUSTOMER_KEY = "fk_customer";
    protected static final String ORDER_DATE = "order_date";
    protected static final String ORDER_CART = "cart";

    protected static final String RECIPE_ID = "recipe_id";
    protected static final String RECIPE_MEDICINE = "medicine";
    protected static final String RECIPE_DOSAGE = "dosage";
    protected static final String RECIPE_DOCTOR_ID = "doctor_id";
    protected static final String RECIPE_CUSTOMER_ID = "fk_customer";
    protected static final String RECIPE_MIO_ID = "id_medicine_in_order";  // MIO - medicine in order
    protected static final String RECIPE_VALIDITY = "validity";  // MIO - medicine in order
    protected static final String RECIPE_NEED_EXTENSION = "need_extension";  // MIO - medicine in order
}
