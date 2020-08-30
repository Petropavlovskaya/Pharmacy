package by.petropavlovskaja.pharmacy.dao;

import by.petropavlovskaja.pharmacy.dao.sql.MedicineInOrderSQL;
import by.petropavlovskaja.pharmacy.db.ConnectionPool;
import by.petropavlovskaja.pharmacy.model.Medicine;
import by.petropavlovskaja.pharmacy.model.MedicineInOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import static by.petropavlovskaja.pharmacy.dao.DatabaseColumnNameConstant.*;

/**
 * Class for executing SQL queries to the database related to the medicine in order
 */
public class MedicineInOrderDAO {
    private static Logger logger = LoggerFactory.getLogger(MedicineInOrderDAO.class);

    /**
     * String property for logger message
     */
    private String loggerMessage;

    /**
     * Constructor - create INSTANCE of class
     */
    private MedicineInOrderDAO() {
    }

    /**
     * Nested class create instance of the class
     */
    private static class MedicineInOrderDAOHolder {
        public static final MedicineInOrderDAO medicineInOrderDAO = new MedicineInOrderDAO();
    }

    /**
     * The method for get instance of the class
     *
     * @return - class instance
     */
    public static MedicineInOrderDAO getInstance() {
        return MedicineInOrderDAOHolder.medicineInOrderDAO;
    }


    /**
     * The method inserts a medicine in order into the database
     *
     * @param orderId  - order ID
     * @param medicine - medicine
     * @param quantity - quantity
     * @return - ID record of insert medicine if insert was successful or ID = -1
     */
    public int createMedicineInOrder(int orderId, Medicine medicine, int quantity) {
        int idInsertMedicine = -1;
        int countInsertRowsLogin;
        int columnNumber = 1;
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(MedicineInOrderSQL.INSERT_MEDICINE_IN_ORDER.getQuery(), Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setString(columnNumber++, medicine.getName());
            statement.setString(columnNumber++, medicine.getDosage());
            statement.setDate(columnNumber++, new java.sql.Date(medicine.getExpDate().getTime()));
            statement.setBoolean(columnNumber++, medicine.isRecipeRequired());
            statement.setInt(columnNumber++, medicine.getIndivisibleAmount());
            statement.setInt(columnNumber++, quantity);
            statement.setInt(columnNumber++, medicine.getPrice());
            statement.setInt(columnNumber, orderId);
            countInsertRowsLogin = statement.executeUpdate();
            if (countInsertRowsLogin != 1) {
                loggerMessage = "Can't add Medicine: " + medicine.toString() + " into table Cart/Order id = " + orderId;
                logger.error(loggerMessage);
            } else {
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) {
                    idInsertMedicine = resultSet.getInt(1);
                    loggerMessage = "Insert into table Active_med_in_cart complete. Insert Medicine id= " + idInsertMedicine;
                    logger.info(loggerMessage);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idInsertMedicine;
    }


    /**
     * The method updates in the database a price of each medicine in the cart
     *
     * @param cartId             - cart ID
     * @param medicineInOrderSet - set of medicines in the cart
     */
    public void updateMedicinePriceInCart(int cartId, Set<MedicineInOrder> medicineInOrderSet) {
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(MedicineInOrderSQL.UPDATE_PRICE_AMOUNT_IN_CART_BY_ID_AND_CART_ID.getQuery())
        ) {
            for (MedicineInOrder medicineItem : medicineInOrderSet) {
                statement.setInt(1, medicineItem.getPriceForOne());
                statement.setInt(2, medicineItem.getQuantity());
                statement.setInt(3, medicineItem.getId());
                int countUpdateRowsMedicine = statement.executeUpdate();
                if (countUpdateRowsMedicine != 1) {
                    loggerMessage = "Update into table MedicineInOrder is failed. We update for: " + medicineItem.toString() + " rows = " + countUpdateRowsMedicine + ".";
                    logger.error(loggerMessage);
                } else {
                    loggerMessage = "Update into table MedicineInOrder complete. We update next medicine data: " + medicineItem.toString();
                    logger.info(loggerMessage);
                }
            }
        } catch (SQLException e) {
            loggerMessage = "SQL Exception in method updateMedicinePriceInCart. Cart id = " + cartId + ". ";
            logger.trace(loggerMessage, e);
        }
    }

    /**
     * The method updates quantity of medicines in the cart
     *
     * @param idMedicine - medicine ID
     * @param quantity   - medicine quantity
     */
    public void updateMedicineInCart(int idMedicine, int quantity) {
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(MedicineInOrderSQL.UPDATE_MEDICINES_QUANTITY_IN_CART_BY_ID.getQuery())
        ) {
            statement.setInt(1, quantity);
            statement.setInt(2, idMedicine);
            int countUpdateRowsMedicine = statement.executeUpdate();
            if (countUpdateRowsMedicine != 1) {
                loggerMessage = "Update into table MedicineInOrder is failed. Row for update id = " + idMedicine + ". Was updated " + countUpdateRowsMedicine + " rows.";
                logger.error(loggerMessage);
            } else {
                loggerMessage = "Update into table MedicineInOrder complete. We update next medicine id = " + idMedicine;
                logger.info(loggerMessage);
            }
        } catch (SQLException e) {
            loggerMessage = "SQL Exception when update medicine in cart. Medicine id = " + idMedicine + ". ";
            logger.error(loggerMessage, e);
        }
    }

    /**
     * The method deletes the medicines from the cart
     *
     * @param medicineId - medicine ID
     */
    public void deleteMedicineFromOrder(int medicineId) {
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(MedicineInOrderSQL.DELETE_MEDICINE_BY_ID.getQuery())
        ) {
            statement.setInt(1, medicineId);
            int countUpdateRowsMedicine = statement.executeUpdate();
            if (countUpdateRowsMedicine != 1) {
                loggerMessage = "Delete from table MedicineInOrder is failed. There Was deleted " + countUpdateRowsMedicine + " rows.";
                logger.error(loggerMessage);
            } else {
                loggerMessage = "Delete from table MedicineInOrder complete. We delete next medicine id = " + medicineId;
                logger.info(loggerMessage);
            }
        } catch (SQLException e) {
            loggerMessage = "SQL Exception in delete medicine. Medicine Id = " + medicineId + ". ";
            logger.error(loggerMessage, e);
        }
    }

    /**
     * The method finds a medicine in the order
     *
     * @param medicine - medicine
     * @param orderId  - order ID
     * @return - medicine in order instance
     */
    public MedicineInOrder findMedicineInOrderByMedicine(Medicine medicine, int orderId) {
        int columnNumber = 1;
        MedicineInOrder resultMedicine = new MedicineInOrder(-1);
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(MedicineInOrderSQL.FIND_MEDICINE_IN_CART_BY_NAME_DOSAGE_AMOUNT.getQuery())
        ) {
            statement.setString(columnNumber++, medicine.getName());
            statement.setString(columnNumber++, medicine.getDosage());
            statement.setInt(columnNumber++, medicine.getIndivisibleAmount());
            statement.setInt(columnNumber, orderId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    resultMedicine = createMedicineInOrderFromDB(rs, false);
                }
            }
        } catch (SQLException e) {
            loggerMessage = "SQL Exception in method findMedicineInOrderByMedicine with medicine: id = " + medicine.getId()
                    + ", name = " + medicine.getName() + ", dosage = " + medicine.getDosage() + ". ";
            logger.trace(loggerMessage, e);
        }
        return resultMedicine;
    }


    /**
     * The method finds customer's medicines details in the cart and can update price in it
     *
     * @param cartId          - cart ID
     * @param needActualPrice - is prices need to update
     * @return - set of medicines in the cart
     */
    public Set<MedicineInOrder> findMedicineInCartWithActualPrice(int cartId,
                                                                  boolean needActualPrice) {
        Comparator<MedicineInOrder> comparator = new MedicineInOrder.NameComparator().thenComparing(
                new MedicineInOrder.DosageComparator().thenComparing(new MedicineInOrder.PriceComparator()));
        Set<MedicineInOrder> cartDetails = new TreeSet<>(comparator);
        String sql;
        if (needActualPrice) {
            sql = MedicineInOrderSQL.FIND_ACTUAL_PRICE_FOR_MEDICINES_IN_CART_BY_CART_ID.getQuery();
        } else {
            sql = MedicineInOrderSQL.FIND_MEDICINE_IN_CART_BY_ORDER_ID.getQuery();
        }
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setInt(1, cartId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    cartDetails.add(createMedicineInOrderFromDB(rs, needActualPrice));
                }
            }
        } catch (
                SQLException e) {
            loggerMessage = "SQL Exception in method findMedicineInCartWithActualPrice for cart id = " + cartId;
            logger.trace(loggerMessage, e);
            e.printStackTrace();
        }
        return cartDetails;
    }

    /**
     * The method creates medicines in the order instance from ResultSet
     *
     * @param rs - ResultSet
     * @return - Order instance if account was found or Order with id = -1 if wasn't
     */
    MedicineInOrder createMedicineInOrderFromDB(ResultSet rs,
                                                boolean needTotalAmountFromMedicineList) {
        MedicineInOrder medicineInOrder = new MedicineInOrder(-1);
        try {
            medicineInOrder = new MedicineInOrder(rs.getInt(MIO_ID), rs.getString(MIO_MEDICINE),
                    rs.getInt(MIO_INDIVISIBLE_AMOUNT), rs.getString(MIO_DOSAGE), rs.getDate(MIO_EXP_DATE),
                    rs.getBoolean(MIO_RECIPE_REQUIRED), rs.getInt(MIO_QUANTITY), rs.getInt(MIO_PRICE), rs.getInt(MIO_ORDER_KEY));
            if (needTotalAmountFromMedicineList) {
                medicineInOrder.setAmount(rs.getInt(MIO_AMOUNT));
            }
            medicineInOrder.setRubCoin();
        } catch (SQLException e) {
            logger.trace("SQL Exception in method createMedicineInOrderFromDB", e);
        }
        return medicineInOrder;
    }
}
