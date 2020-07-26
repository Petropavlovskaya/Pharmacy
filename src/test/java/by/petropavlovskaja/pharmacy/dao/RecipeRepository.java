package by.petropavlovskaja.pharmacy.dao;

import by.petropavlovskaja.pharmacy.Repository;
import by.petropavlovskaja.pharmacy.model.Recipe;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecipeRepository extends Repository<Recipe> {
    protected static final List<Recipe> RECIPES = new ArrayList<>();
    private static final String CREATE_TABLE = "CREATE TABLE recipe (recipe_id int4 NOT NULL GENERATED ALWAYS AS IDENTITY, " +
            "medicine varchar NOT NULL, doctor_id int4 NULL, fk_customer int4 NOT NULL,id_medicine_in_order int4 NULL, " +
            "validity date NULL, need_extension bool NOT NULL DEFAULT false, dosage varchar NULL, " +
            "CONSTRAINT recipe_un UNIQUE (medicine, doctor_id, fk_customer, id_medicine_in_order, validity, dosage));";
    static final String INSERT_RECIPE_SQL = "INSERT INTO recipe (medicine, doctor_id, fk_customer, id_medicine_in_order, validity, need_extension, dosage)\n" +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

    public RecipeRepository() throws SQLException {
        super();
    }

    public void initializeMembers() throws ParseException {
        Date expDate;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        expDate = format.parse("2020-10-20");
        RECIPES.add(new Recipe(1, "New", "15/26", 1, 1, 1, expDate, true));
        RECIPES.add(new Recipe(2, "Гроприносин", "500 мг", 1, 2, 1, expDate, false));
        RECIPES.add(new Recipe(3, "АмброГексал", "7,5 мг/мл", 1, 2, 1, expDate, false));
        RECIPES.add(new Recipe(4, "АмброГексал", "7,5 мг/мл", 1, 3, 0, expDate, true));
        RECIPES.add(new Recipe(5, "АмброГексал", "7,5 мг/мл", 1, 3, 3, expDate, false));
    }

    @Override
    public void createTable() throws SQLException {
        Statement createStatement = connection.createStatement();
        createStatement.execute(CREATE_TABLE);
    }

    @Override
    public void insertMembers() throws SQLException {
        final PreparedStatement insertMembers = connection.prepareStatement(INSERT_RECIPE_SQL);
        RECIPES.forEach(member -> insertMember(member, insertMembers));
        insertMembers.executeBatch();
    }

    private static void insertMember(Recipe recipe, PreparedStatement insertMembers) {
        try {
            insertMembers.setString(1, recipe.getMedicine());
            insertMembers.setInt(2, recipe.getDoctorID());
            insertMembers.setInt(3, recipe.getFkCustomer());
            insertMembers.setInt(4, recipe.getIdMedicineInOrder());
            insertMembers.setDate(5, new java.sql.Date(recipe.getValidity().getTime()));
            insertMembers.setBoolean(6, recipe.isNeedExtension());
            insertMembers.setString(7, recipe.getDosage());
            insertMembers.addBatch();
        } catch (SQLException e) {
            throw new UnsupportedOperationException(e.getMessage());
        }
    }

    public void closeConnection() throws SQLException {
        connection.createStatement().execute("DROP TABLE recipe");
    }

}
