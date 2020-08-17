package by.petropavlovskaja.pharmacy.dao;

import by.petropavlovskaja.pharmacy.dao.sql.RecipeSQL;
import by.petropavlovskaja.pharmacy.db.ConnectionPool;
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

import static by.petropavlovskaja.pharmacy.dao.DatabaseColumnNameConstant.*;

/**
 * Class for executing SQL queries to the database related to the recipe
 */
public class RecipeDAO {

    /**
     * String property for logger message
     */
    private String loggerMessage;
    private static Logger logger = LoggerFactory.getLogger(RecipeDAO.class);

    /**
     * Constructor - create INSTANCE of class
     */
    private RecipeDAO() {
    }

    /**
     * Nested class create instance of the class
     */
    private static class RecipeDAOHolder {
        public static final RecipeDAO RECIPE_DAO = new RecipeDAO();
    }

    /**
     * The method for get instance of the class
     *
     * @return - class instance
     */
    public static RecipeDAO getInstance() {
        return RecipeDAOHolder.RECIPE_DAO;
    }

    /**
     * The method finds all customer's recipes in the database
     *
     * @param customerId - customer ID
     * @return - a set of recipes
     */
    public Set<Recipe> getAllCustomerRecipe(int customerId) {
        Comparator<Recipe> comp = new Recipe.RecipeNameComparator().thenComparing(new Recipe.RecipeDosageComparator()
                .thenComparing(new Recipe.RecipeOrderIdComparator()));
        Set<Recipe> recipes = new TreeSet<>(comp);
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(RecipeSQL.GET_ALL_RECIPE_BY_CUSTOMER_ID.getQuery())
        ) {
            statement.setInt(1, customerId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    recipes.add(createRecipeFromDB(rs, false));
                }
            }
        } catch (
                SQLException e) {
            logger.trace("SQL exception in a method getAllCustomerRecipe. ", e);
            e.printStackTrace();
        }
        return recipes;
    }

    /**
     * The method delete a recipe from the database
     *
     * @param recipeId - a recipe ID
     */
    public void deleteRecipe(int recipeId) {
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(RecipeSQL.DELETE_RECIPE_BY_ID.getQuery())
        ) {
            statement.setInt(1, recipeId);
            int countUpdateRowsMedicine = statement.executeUpdate();
            if (countUpdateRowsMedicine != 1) {
                loggerMessage = "Update table Recipe is failed. We delete for recipeId: " + recipeId + " " + countUpdateRowsMedicine + " rows.";
                logger.trace(loggerMessage);
            } else {
                loggerMessage = "Update table Recipe complete. We delete next recipeId: " + recipeId;
                logger.info(loggerMessage);
            }
        } catch (
                SQLException e) {
            logger.trace("SQL exception in a method deleteRecipe. ", e);
            e.printStackTrace();
        }
    }

    /**
     * The method for setting the field id_medicine_in_order = -1 into the database field as a refuse criterion when extension the recipe
     *
     * @param accountId - a customer ID
     * @param recipeId  - a recipe ID
     */
    public void refuseRecipe(int accountId, int recipeId) {
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(RecipeSQL.UPDATE_REFUSE_RECIPE_BY_ID.getQuery())
        ) {
            statement.setInt(1, accountId);
            statement.setInt(2, recipeId);
            int countUpdateRowsMedicine = statement.executeUpdate();
            if (countUpdateRowsMedicine != 1) {
                loggerMessage = "Update table Recipe is failed. We set id_medicine_in_order=0 for recipeId: " + recipeId + " " + countUpdateRowsMedicine + " rows.";
                logger.trace(loggerMessage);
            } else {
                loggerMessage = "Update table Recipe complete. We set id_medicine_in_order=0 next recipeId: " + recipeId;
                logger.info(loggerMessage);
            }
        } catch (
                SQLException e) {
            logger.trace("SQL exception in a method refuseRecipe. ", e);
            e.printStackTrace();
        }
    }

    /**
     * The method finds all valid customer's recipes in the database
     *
     * @param customerId - a customer ID
     * @return - a set of recipes
     */
    public Set<Recipe> getAllValidRecipe(int customerId) {
        Comparator<Recipe> comp = new Recipe.RecipeNameComparator().thenComparing(new Recipe.RecipeDosageComparator()
                .thenComparing(new Recipe.RecipeOrderIdComparator()));
        Set<Recipe> recipes = new TreeSet<>(comp);
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(RecipeSQL.GET_ALL_ACTIVE_RECIPE_BY_CUSTOMER_ID.getQuery())
        ) {
            statement.setInt(1, customerId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    recipes.add(createRecipeFromDB(rs, false));
                }
            }
        } catch (
                SQLException e) {
            logger.trace("SQL exception in a method getAllValidRecipe. ", e);
            e.printStackTrace();
        }
        return recipes;
    }

    /**
     * The method finds in the database all customers's recipes that need an extension
     *
     * @return - a set of recipes
     */
    public Set<Recipe> getAllOrdered() {
        Comparator<Recipe> comp = new Recipe.RecipeCustomerComparator().thenComparing(new Recipe.RecipeNameComparator())
                .thenComparing(new Recipe.RecipeDosageComparator());
        Set<Recipe> recipes = new TreeSet<>(comp);
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery(RecipeSQL.GET_ALL_ORDERED_RECIPE.getQuery())
        ) {
            while (rs.next()) {
                recipes.add(createRecipeFromDB(rs, true));
            }
        } catch (
                SQLException e) {
            logger.trace("SQL exception in a method getAllOrdered. ", e);
            e.printStackTrace();
        }
        return recipes;
    }

    /**
     * The method inserts into the database new customer's recipe that need an extension
     *
     * @param medicineName - a medicine name
     * @param dosage       - a medicine dosage
     * @param customerId   - a customer ID
     */
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
                loggerMessage = "Insert into table Recipe is failed. We insert: " + countInsertRowsRecipe + " rows for recipe: " + medicineName;
                logger.trace(loggerMessage);
            } else {
                loggerMessage = "Insert into table Recipe complete. We insert next medicine data: " + medicineName;
                logger.info(loggerMessage);
            }
        } catch (
                SQLException e) {
            logger.trace("SQL exception in a method insertRecipeCustomer. ", e);
            e.printStackTrace();
        }
    }

    /**
     * The method inserts into the database new recipe for customer
     *
     * @param medicineName - a medicine name
     * @param dosage       - a medicine dosage
     * @param customerId   - a customer ID
     * @param pharmacistId - a pharmacist ID
     * @param date         - a recipe validity date
     */
    public void insertRecipeDoctor(String medicineName, String dosage, int customerId, int pharmacistId, Date date) {
        int columnNumber = 1;
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(RecipeSQL.INSERT_RECIPE_DOCTOR.getQuery())
        ) {
            statement.setString(columnNumber++, medicineName);
            statement.setString(columnNumber++, dosage);
            statement.setInt(columnNumber++, pharmacistId);
            statement.setInt(columnNumber++, customerId);
            statement.setDate(columnNumber, new java.sql.Date(date.getTime()));
            int countInsertRowsRecipe = statement.executeUpdate();
            if (countInsertRowsRecipe != 1) {
                loggerMessage = "Insert into table Recipe is failed. We insert: " + countInsertRowsRecipe + " rows for recipe: " + medicineName;
                logger.trace(loggerMessage);
            } else {
                loggerMessage = "Insert into table Recipe complete. We insert next medicine data: " + medicineName;
                logger.info(loggerMessage);
            }
        } catch (
                SQLException e) {
            logger.trace("SQL exception in a method insertRecipeDoctor. ", e);
            e.printStackTrace();
        }
    }

    /**
     * The method updates in the database recipe. It setting for recipe with invalidity date status "need an extension"
     *
     * @param recipeId - a recipe ID
     */
    public void setNeedExtensionByID(int recipeId) {
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(RecipeSQL.UPDATE_NEED_EXTENSION_BY_RECIPE_ID.getQuery())
        ) {
            statement.setInt(1, recipeId);
            int countUpdateRowsRecipe = statement.executeUpdate();
            if (countUpdateRowsRecipe != 1) {
                loggerMessage = "Update into table Recipe is failed. We update: " + countUpdateRowsRecipe + " rows for recipeId: " + recipeId;
                logger.trace(loggerMessage);
            } else {
                loggerMessage = "Update into table Recipe complete. We update data for next recipeId: " + recipeId;
                logger.info(loggerMessage);
            }
        } catch (SQLException e) {
            logger.error("SQL Exception in a method setNeedExtensionByID. ", e);
        }
    }

    /**
     * The method updates in the database recipe. It setting validity date for recipe that needed an extension
     *
     * @param recipeId - a recipe ID
     * @param doctorId - a doctor ID
     * @param validFor - a recipe validity date
     */
    public void validateRecipe(int recipeId, Date validFor, int doctorId) {
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(RecipeSQL.VALIDATE_RECIPE.getQuery())
        ) {
            statement.setDate(1, new java.sql.Date(validFor.getTime()));
            statement.setInt(2, doctorId);
            statement.setInt(3, recipeId);
            int countUpdateRowsRecipe = statement.executeUpdate();
            if (countUpdateRowsRecipe != 1) {
                loggerMessage = "Update into table Recipe is failed. We update: " + countUpdateRowsRecipe + " rows for recipeId: " + recipeId;
                logger.trace(loggerMessage);
            } else {
                loggerMessage = "Update into table Recipe complete. We update data for next recipeId: " + recipeId;
                logger.info(loggerMessage);
            }
        } catch (SQLException e) {
            logger.trace("SQL Exception in a method validateRecipe. ", e);
        }
    }

    /**
     * The method creates recipe instance from ResultSet
     *
     * @param rs - ResultSet
     * @return - Recipe instance if recipe was found or Recipe instance with id = -1 if wasn't
     */
    private Recipe createRecipeFromDB(ResultSet rs, boolean isFioNeed) {
        Recipe recipe = new Recipe(-1);
        try {
            recipe = new Recipe(rs.getInt(RECIPE_ID), rs.getString(RECIPE_MEDICINE),
                    rs.getString(RECIPE_DOSAGE), rs.getInt(RECIPE_DOCTOR_ID),
                    rs.getInt(RECIPE_CUSTOMER_ID), rs.getInt(RECIPE_MIO_ID),
                    rs.getTimestamp(RECIPE_VALIDITY), rs.getBoolean(RECIPE_NEED_EXTENSION));
            if (isFioNeed) {
                recipe.setCustomerFio(rs.getString(ACCOUNT_SURNAME) + " " +
                        rs.getString(ACCOUNT_NAME) + " " + rs.getString(ACCOUNT_PATRONYMIC));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recipe;
    }
}