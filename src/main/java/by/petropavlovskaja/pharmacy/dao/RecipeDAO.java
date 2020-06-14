package by.petropavlovskaja.pharmacy.dao;

import by.petropavlovskaja.pharmacy.dao.sql.RecipeSQL;
import by.petropavlovskaja.pharmacy.db.impl.ConnectionPool;
import by.petropavlovskaja.pharmacy.model.Recipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

public class RecipeDAO {
    private static Logger logger = LoggerFactory.getLogger(RecipeDAO.class);

    private RecipeDAO() {
    }

    private static class RecipeDAOHolder {
        public static final RecipeDAO RECIPE_DAO = new RecipeDAO();
    }

    public static RecipeDAO getInstance() {
        return RecipeDAOHolder.RECIPE_DAO;
    }


    public Set<Recipe> getAllForCustomer(int customerId) {
        Comparator<Recipe> comp = new Recipe.RecipeNameComparator().thenComparing(new Recipe.RecipeDosageComparator()
                .thenComparing(new Recipe.RecipeOrderIdComparator()));
        Set<Recipe> recipes = new TreeSet(comp);
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(RecipeSQL.GET_ALL_RECIPE_BY_CUSTOMER_ID.getQuery())
        ) {
            statement.setInt(1, customerId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                recipes.add(createRecipeFromDB(rs, false));
            }
        } catch (
                SQLException e) {
            System.out.println("Nothing was find ((");
            e.printStackTrace();
        }
        return recipes;
    }

    public void deleteRecipe(int recipeId) {
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(RecipeSQL.DELETE_RECIPE_BY_ID.getQuery())
        ) {
            statement.setInt(1, recipeId);
            int countUpdateRowsMedicine = statement.executeUpdate();
            if (countUpdateRowsMedicine != 1) {
                logger.error("Update table Recipe is failed. We delete for recipeId: " + recipeId + " " + countUpdateRowsMedicine + " rows.");
            } else {
                logger.info("Update table Recipe complete. We delete next recipeId: " + recipeId);
            }
        } catch (
                SQLException e) {
            System.out.println("Nothing was find ((");
            e.printStackTrace();
        }
    }

    public Set<Recipe> getAllValidRecipe(int customerId) {
        Comparator<Recipe> comp = new Recipe.RecipeNameComparator().thenComparing(new Recipe.RecipeDosageComparator()
                .thenComparing(new Recipe.RecipeOrderIdComparator()));
        Set<Recipe> recipes = new TreeSet(comp);
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(RecipeSQL.GET_ALL_ACTIVE_RECIPE_BY_CUSTOMER_ID.getQuery())
        ) {
            statement.setInt(1, customerId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                recipes.add(createRecipeFromDB(rs, false));
            }
        } catch (
                SQLException e) {
            System.out.println("Nothing was find ((");
            e.printStackTrace();
        }
        return recipes;
    }

    public Set<Recipe> getAllOrdered() {
        Comparator<Recipe> comp = new Recipe.RecipeNameComparator().thenComparing(new Recipe.RecipeDosageComparator());
        Set<Recipe> recipes = new TreeSet(comp);
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery(RecipeSQL.GET_ALL_ORDERED_RECIPE.getQuery());
        ) {
            while (rs.next()) {
                recipes.add(createRecipeFromDB(rs, true));
            }
        } catch (
                SQLException e) {
            System.out.println("Nothing was find ((");
            e.printStackTrace();
        }
        return recipes;
    }

    public void insertRecipeCustomer(String medicineName, String dosage, int customerId) {
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(RecipeSQL.INSERT_RECIPE_CUSTOMER.getQuery())
        ) {
            statement.setString(1, medicineName);
            statement.setString(2, dosage);
            statement.setInt(3, customerId);
            int countInsertRowsRecipe = statement.executeUpdate();
            if (countInsertRowsRecipe != 1) {
                logger.error("Insert into table Recipe is failed. We insert: " + countInsertRowsRecipe + " rows for recipe: " + medicineName);
            } else {
                logger.info("Insert into table Recipe complete. We insert next medicine data: " + medicineName);
            }
        } catch (
                SQLException e) {
            System.out.println("Nothing was find ((");
            e.printStackTrace();
        }
    }

    public void insertRecipeDoctor(String medicineName, String dosage, int customerId, int pharmacistId, Date date) {
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(RecipeSQL.INSERT_RECIPE_DOCTOR.getQuery())
        ) {
            statement.setString(1, medicineName);
            statement.setString(2, dosage);
            statement.setInt(3, pharmacistId);
            statement.setInt(4, customerId);
            statement.setDate(5, new java.sql.Date(date.getTime()));
            int countInsertRowsRecipe = statement.executeUpdate();
            if (countInsertRowsRecipe != 1) {
                logger.error("Insert into table Recipe is failed. We insert: " + countInsertRowsRecipe + " rows for recipe: " + medicineName);
            } else {
                logger.info("Insert into table Recipe complete. We insert next medicine data: " + medicineName);
            }
        } catch (
                SQLException e) {
            System.out.println("Nothing was find ((");
            e.printStackTrace();
        }
    }

    public void setNeedExtensionByID(int recipeId) {
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(RecipeSQL.UPDATE_NEED_EXTENSION_BY_RECIPE_ID.getQuery());
        ) {
            statement.setInt(1, recipeId);
            int countUpdateRowsRecipe = statement.executeUpdate();
            if (countUpdateRowsRecipe != 1) {
                logger.error("Update into table Recipe is failed. We update: " + countUpdateRowsRecipe + " rows for recipeId: " + recipeId);
            } else {
                logger.info("Update into table Recipe complete. We update data for next recipeId: " + recipeId);
            }
        } catch (SQLException e) {
            logger.error("SQL Exception in create medicine: " + e);
        }
    }

    public void validateRecipe(int recipeId, Date validFor, int doctorId) {
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(RecipeSQL.VALIDATE_RECIPE.getQuery());
        ) {
            statement.setDate(1, new java.sql.Date(validFor.getTime()));
            statement.setInt(2, doctorId);
            statement.setInt(3, recipeId);
            int countUpdateRowsRecipe = statement.executeUpdate();
            if (countUpdateRowsRecipe != 1) {
                logger.error("Update into table Recipe is failed. We update: " + countUpdateRowsRecipe + " rows for recipeId: " + recipeId);
            } else {
                logger.info("Update into table Recipe complete. We update data for next recipeId: " + recipeId);
            }
        } catch (SQLException e) {
            logger.error("SQL Exception in create medicine: " + e);
        }
    }


    private Recipe createRecipeFromDB(ResultSet rs, boolean isFioNeed) {
        Recipe recipe = new Recipe(-1);
        try {
            recipe = new Recipe(rs.getInt("recipe_id"), rs.getString("medicine"),
                    rs.getString("dosage"), rs.getInt("doctor_id"),
                    rs.getInt("fk_customer"), rs.getInt("id_medicine_in_order"),
                    rs.getTimestamp("validity"), rs.getBoolean("need_extension"));
            if (isFioNeed) {
                recipe.setCustomerFio(rs.getString("surname") + " " +
                        rs.getString("name") + " " + rs.getString("patronymic"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recipe;
    }
}