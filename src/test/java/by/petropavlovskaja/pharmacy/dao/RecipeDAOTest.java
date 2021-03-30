package by.petropavlovskaja.pharmacy.dao;

import by.petropavlovskaja.pharmacy.dao.sql.RecipeSQL;
import by.petropavlovskaja.pharmacy.model.Recipe;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class RecipeDAOTest {
    private static final String SELECT_RECIPES = "SELECT * FROM recipe";
    private static RecipeRepository h2Repository;

    public RecipeDAOTest() {
    }

    @BeforeAll
    public static void setupDatabase() throws SQLException, ParseException {
        h2Repository = new RecipeRepository();
        h2Repository.initializeMembers();
        h2Repository.createTable();
        h2Repository.insertMembers();
    }

    @AfterAll
    public static void
    tearDownDatabase() throws SQLException {
        h2Repository.closeConnection();
    }

    @Test
    public void
    correctlyInsertsMembersIntoDatabase() throws SQLException, ParseException {
        List<Recipe> dbList = new ArrayList<>();
        Statement selectStatement = RecipeRepository.connection.createStatement();
        ResultSet membersResultSet = selectStatement.executeQuery(SELECT_RECIPES);
        while (membersResultSet.next()) {
            dbList.add(getRecipeFromDB(membersResultSet));
        }
        assertEquals(RecipeRepository.RECIPES, dbList);
    }

    @Test
    void getAllCustomerRecipe() throws SQLException, ParseException {
        List<Recipe> testList = new ArrayList<>();
        testList.add(RecipeRepository.RECIPES.get(1));
        testList.add(RecipeRepository.RECIPES.get(2));

        List<Recipe> dbList = new ArrayList<>();
        PreparedStatement ps = RecipeRepository.connection.prepareStatement(RecipeSQL.GET_ALL_RECIPE_BY_CUSTOMER_ID.getQuery());
        ps.setInt(1, 2);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            dbList.add(getRecipeFromDB(rs));
        }

        assertEquals(testList, dbList);
    }

    @Test
    void refuseRecipe() throws SQLException, ParseException {
        Recipe testRecipe = RecipeRepository.RECIPES.get(1);
        testRecipe.setIdMedicineInOrder(-1);

        Recipe dbRecipe = new Recipe(-1);
        PreparedStatement ps = RecipeRepository.connection.prepareStatement(RecipeSQL.UPDATE_REFUSE_RECIPE_BY_ID.getQuery());
        ps.setInt(1, 1);
        ps.setInt(2, 2);
        ps.executeUpdate();

        ps = RecipeRepository.connection.prepareStatement("SELECT * FROM recipe WHERE recipe_id = 2;");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            dbRecipe = getRecipeFromDB(rs);
        }

        assertEquals(testRecipe, dbRecipe);

    }

    @Test
    void getAllValidRecipe() throws SQLException, ParseException {
        List<Recipe> testList = new ArrayList<>();
        testList.add(RecipeRepository.RECIPES.get(3));

        List<Recipe> dbList = new ArrayList<>();

        PreparedStatement ps = RecipeRepository.connection.prepareStatement("SELECT * FROM recipe WHERE id_medicine_in_order = 0 AND " +
                "validity > current_date AND fk_customer = ?;");
        ps.setInt(1, 3);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            dbList.add(getRecipeFromDB(rs));
        }

        assertEquals(testList, dbList);
    }

    @Test
    void getAllOrdered() throws SQLException, ParseException {
        List<Recipe> testList = new ArrayList<>();
        testList.add(RecipeRepository.RECIPES.get(3));

        List<Recipe> dbList = new ArrayList<>();
        Statement st = RecipeRepository.connection.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM recipe WHERE need_extension=true AND id_medicine_in_order=0;");
        while (rs.next()) {
            dbList.add(getRecipeFromDB(rs));
        }

        assertEquals(testList, dbList);
    }

    @Test
    void setNeedExtensionByID() throws SQLException, ParseException {
//        MEMBERS.add(new Recipe(5, "АмброГексал", "7,5 мг/мл", 1, 3, 3, expDate, false));
        RecipeRepository.RECIPES.get(4).setNeedExtension(true);
//        testRecipe.setNeedExtension(true);

        Recipe dbRecipe = new Recipe(-1);
//        UPDATE_NEED_EXTENSION_BY_RECIPE_ID("UPDATE recipe SET need_extension=true, validity=null WHERE recipe_id=?;"),
        PreparedStatement ps = RecipeRepository.connection.prepareStatement(RecipeSQL.UPDATE_NEED_EXTENSION_BY_RECIPE_ID.getQuery());
        ps.setInt(1, 5);
        ps.executeUpdate();

        ps = RecipeRepository.connection.prepareStatement("SELECT * FROM recipe WHERE recipe_id = 5;");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            dbRecipe = getRecipeFromDB(rs);
        }

        assertEquals(RecipeRepository.RECIPES.get(4).isNeedExtension(), dbRecipe.isNeedExtension());
    }

/*    @Test
    void validateRecipe() throws ParseException, SQLException {
        int id = getRandomId();
//        VALIDATE_RECIPE("UPDATE recipe SET need_extension=false, validity=?, doctor_id=? WHERE recipe_id=?;"),
        RecipeRepository.MEMBERS.get(1).setNeedExtension(true);
        Date expDate;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        expDate = format.parse("2020-10-20");
        Recipe testRecipe = new Recipe(id, "АмброГексал", "7,5 мг/мл", 55, 3, 3, expDate, true);
        //insert test row
        insertTestItem(testRecipe);

//        PreparedStatement ps = RecipeRepository.connection.prepareStatement("UPDATE recipe SET need_extension=false, validity=?, doctor_id=? WHERE recipe_id=?;");
        PreparedStatement ps = RecipeRepository.connection.prepareStatement(RecipeSQL.VALIDATE_RECIPE.getQuery());
        ps.setDate(1, new java.sql.Date(expDate.getTime()));
        ps.setInt(2, 8);
        ps.setInt(3, id);
        ps.executeUpdate();

        Recipe dbRecipe = new Recipe(-1);
        ps = RecipeRepository.connection.prepareStatement("SELECT * FROM recipe WHERE recipe_id = " + id);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            dbRecipe = getRecipeFromDB(rs);
        }
//
        List<Recipe> dbList = new ArrayList<>();
        Statement selectStatement = RecipeRepository.connection.createStatement();
        ResultSet membersResultSet = selectStatement.executeQuery(SELECT_MEMBERS);
        while (membersResultSet.next()) {
            dbList.add(getRecipeFromDB(membersResultSet));
        }
//
        for (Recipe recipe : dbList) {
            System.out.println(recipe.toString());
        }
        assertFalse(dbRecipe.isNeedExtension());
        System.out.println(dbRecipe.toString());
        assertEquals(8, dbRecipe.getDoctorID());

        deleteTestItem(testRecipe);
    }*/

    @Test
    void deleteRecipe() {
    }

    private Recipe getRecipeFromDB(ResultSet rs) throws SQLException, ParseException {
        Date expDate;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        expDate = format.parse("2020-10-20");
        return new Recipe(rs.getInt(1), rs.getString(2), rs.getString(8),
                rs.getInt(3), rs.getInt(4), rs.getInt(5), expDate, rs.getBoolean(7));
    }

/*    private void insertTestItem(Recipe recipe) throws SQLException {
        PreparedStatement ps = RecipeRepository.connection.prepareStatement(RecipeRepository.INSERT_MEMBERS);
        RecipeRepository.insertMember(recipe, ps);
    }*/

 /*   private void deleteTestItem(Recipe recipe) throws SQLException {
        PreparedStatement ps = RecipeRepository.connection.prepareStatement("DELETE FROM pharmacy.public.recipe WHERE pharmacy.public.recipe.recipe_id = " + recipe.getId());
        ps.execute();
    }*/

/*    private static int getRandomId() {
        return (int) (10 + Math.random() * 999999);
    }*/
}