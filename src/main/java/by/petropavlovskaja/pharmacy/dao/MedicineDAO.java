package by.petropavlovskaja.pharmacy.dao;

import by.petropavlovskaja.pharmacy.dao.sql.MedicineSQL;
import by.petropavlovskaja.pharmacy.db.ConnectionPool;
import by.petropavlovskaja.pharmacy.model.Medicine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static by.petropavlovskaja.pharmacy.dao.DatabaseColumnNameConstant.*;

/**
 * Class for executing SQL queries to the database related to the medicine
 */
public class MedicineDAO {

    /**
     * String property for logger message
     */
    private String loggerMessage;

    private static Logger logger = LoggerFactory.getLogger(MedicineDAO.class);

    /**
     * Constructor - create INSTANCE of class
     */
    private MedicineDAO() {
    }

    /**
     * Nested class create instance of the class
     */
    private static class MedicineDAOHolder {
        public static final MedicineDAO MEDICINE_DAO = new MedicineDAO();
    }

    /**
     * The method for get instance of the class
     *
     * @return - class instance
     */
    public static MedicineDAO getInstance() {
        return MedicineDAOHolder.MEDICINE_DAO;
    }

    /**
     * The method finds in the database a set of medicines to update the available amount of medicines
     *
     * @param orderId - order ID
     * @return - a set of medicines
     */
    public Set<Medicine> getMedicineDataForChangeAmount(int orderId) {
        Set<Medicine> medicineSet = new HashSet<>();
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(MedicineSQL.GET_INFO_FOR_ORDER_BY_ORDER_ID.getQuery())
        ) {
            statement.setInt(1, orderId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    medicineSet.add(new Medicine(rs.getInt(MEDICINE_ID), rs.getString(MEDICINE_NAME),
                            rs.getString(MEDICINE_DOSAGE), rs.getInt(MEDICINE_PRICE), rs.getInt(MEDICINE_AMOUNT)));
                }
            }
        } catch (
                SQLException e) {
            logger.trace("SQL Exception in method getMedicineDataForChangeAmount. ", e);
            e.printStackTrace();
        }
        return medicineSet;
    }

    /**
     * The method finds a medicine in the database
     *
     * @param medicineId - medicine ID
     * @return - a medicine
     */
    public Medicine findById(Integer medicineId) {
        List<Medicine> medicineList = findBy(MedicineSQL.FIND_MEDICINES_BY_ID.getQuery(), medicineId);
        if (medicineList.size() > 1) {
            return new Medicine(-1);
        }
        return medicineList.get(0);
    }

    /**
     * The method finds all medicines in the database
     *
     * @return - a list of medicines
     */
    public List<Medicine> getAll() {
        List<Medicine> medicineList = new ArrayList<>();
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery(MedicineSQL.GET_ALL_MEDICINES.getQuery())
        ) {
            while (rs.next()) {
                medicineList.add(createMedicineFromDB(rs));
            }
        } catch (SQLException e) {
            logger.debug("SQL Exception in method getAll. ", e);
            e.printStackTrace();
        }
        return medicineList;
    }

    /**
     * The method of getting a number of medicines in the database
     *
     * @return - a count of medicines
     */
    public int getNumberOfRows() {
        List<Medicine> medicineList = getAll();
        return medicineList.size();
    }

    /**
     * The method finds some medicines in the database for certain page
     *
     * @param currentPage    - a number of view page
     * @param recordsPerPage - a number of records per page
     * @return - a list of medicines
     */
    public List<Medicine> findMedicine(int currentPage, int recordsPerPage) {
        List<Medicine> medicineList = new ArrayList<>();
        int start = currentPage * recordsPerPage - recordsPerPage;

        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(MedicineSQL.GET_ALL_FOR_PAGE.getQuery())
        ) {
            statement.setInt(1, start);
            statement.setInt(2, recordsPerPage);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    medicineList.add(createMedicineFromDB(rs));
                }
            }
        } catch (SQLException e) {
            logger.trace("SQL Exception in method findMedicine. ", e);
            e.printStackTrace();
        }
        return medicineList;
    }

    /**
     * The method finds all medicines in the database that require a recipe
     *
     * @return - a set of medicines
     */
    public Set<Medicine> getAllForDoctor() {
        Comparator<Medicine> comp = new Medicine.MedicineNameComparator().thenComparing(new Medicine.MedicineDosageComparator());
        Set<Medicine> medicineSet = new TreeSet<>(comp);
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery(MedicineSQL.GET_ALL_RECIPE_MEDICINES.getQuery())
        ) {
            while (rs.next()) {
                Medicine medicine = new Medicine(rs.getString(MEDICINE_NAME), rs.getString(MEDICINE_DOSAGE));
                medicineSet.add(medicine);
            }
        } catch (
                SQLException e) {
            logger.trace("SQL Exception in method getAllForDoctor. ", e);
            e.printStackTrace();
        }
        return medicineSet;
    }

    /**
     * The method inserts a new medicine into the database
     *
     * @param medicine - a new medicine
     * @return - true if insert was successful
     */
    public boolean create(Medicine medicine) {
        boolean result = false;
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(MedicineSQL.INSERT_MEDICINE.getQuery())

        ) {
            updatePrepareStatement(statement, medicine, false);
            int countInsertRowsMedicine = statement.executeUpdate();
            if (countInsertRowsMedicine != 1) {
                loggerMessage = "Insert into table Medicine is failed. We insert: " + countInsertRowsMedicine + " rows for medicine: " + medicine.toString();
                logger.error(loggerMessage);
            } else {
                result = true;
                loggerMessage = "Insert into table Medicine complete. We insert next medicine data: " + medicine.toString();
                logger.info(loggerMessage);
            }
        } catch (SQLException e) {
            logger.trace("SQL Exception in method create. ", e);
        }
        return result;
    }

    /**
     * The method updates a medicine in the database
     *
     * @param medicine - a new medicine
     * @return - true if insert was successful
     */
    public boolean update(Medicine medicine) {
        boolean result = false;
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(MedicineSQL.UPDATE_MEDICINE.getQuery())
        ) {
            updatePrepareStatement(statement, medicine, true);
            int countUpdateRowsMedicine = statement.executeUpdate();
            if (countUpdateRowsMedicine != 1) {
                loggerMessage = "Update into table Medicine is failed. We update: " + countUpdateRowsMedicine + " rows for medicine: " + medicine.toString();
                logger.error(loggerMessage);
            } else {
                result = true;
                loggerMessage = "Update into table Medicine complete. We update next medicine data: " + medicine.toString();
                logger.info(loggerMessage);
            }
        } catch (SQLException e) {
            logger.trace("SQL Exception in method update. ", e);
        }
        return result;
    }

    /**
     * The method deletes a medicine from the database
     *
     * @param medicine        - a medicine
     * @param pharmacistLogin - a pharmacist's login
     * @return - true if delete was successful
     */
    public boolean deleteById(Medicine medicine, String pharmacistLogin) {
        boolean result = false;
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(MedicineSQL.DELETE_MEDICINE.getQuery())
        ) {
            statement.setInt(1, medicine.getId());
            int countDeleteRowsMedicine = statement.executeUpdate();

            if (countDeleteRowsMedicine != 1) {
                loggerMessage = "Delete from table Medicine: " + medicine.toString() + ". We delete: " + countDeleteRowsMedicine + " rows";
                logger.error(loggerMessage);
            } else {
                result = true;
                loggerMessage = "Pharmacist login = " + pharmacistLogin + " delete Medicine: " + medicine.toString();
                logger.info(loggerMessage);
            }
        } catch (SQLException e) {
            logger.trace("SQL Exception in method deleteById. ", e);
        }
        return result;
    }

    /**
     * The method checks is a medicine in the database
     *
     * @param medicineName - a medicine name
     * @param dosage       - a medicine dosage
     * @return - true if medicine is in the database
     */
    public boolean isMedicine(String medicineName, String dosage) {
        List<Medicine> medicineList = findBy(MedicineSQL.FIND_MEDICINES_BY_NAME_DOSAGE.getQuery(), medicineName, dosage);
        return !medicineList.isEmpty();
    }

    /**
     * The method finds medicines in the database
     *
     * @param query    - SQL query
     * @param criteria - search criteria
     * @return - list of medicines
     */
    private List<Medicine> findBy(String query, Object... criteria) {
        List<Medicine> medicine = new ArrayList<>();
        FindBy findBy = (Connection connection, String sql, Object... values) -> {
            PreparedStatement statement = connection.prepareStatement(sql);
            for (int i = 0; i < values.length; i++) {
                statement.setObject(i + 1, values[i]);
            }
            return statement;
        };

        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = findBy.getPreparedStatement(conn, query, criteria);
                ResultSet rs = statement.executeQuery()
        ) {
            while (rs.next()) {
                medicine.add(createMedicineFromDB(rs));
            }
        } catch (SQLException e) {
            logger.trace("SQL Exception in method findBy. ", e);
            e.printStackTrace();
        }
        return medicine;
    }

    /**
     * The method creates medicine instance from ResultSet
     *
     * @param rs - ResultSet
     * @return - Medicine instance if medicine was found or NULL if wasn't
     */
    private Medicine createMedicineFromDB(ResultSet rs) {
        Medicine medicine = null;
        try {

            medicine = new Medicine(rs.getInt(MEDICINE_ID), rs.getString(MEDICINE_NAME),
                    rs.getInt(MEDICINE_INDIVISIBLE_AMOUNT), rs.getInt(MEDICINE_AMOUNT),
                    rs.getString(MEDICINE_DOSAGE), rs.getDate(MEDICINE_EXP_DATE),
                    rs.getBoolean(MEDICINE_RECIPE_REQUIRED), rs.getInt(MEDICINE_PRICE),
                    rs.getInt(MEDICINE_ADDED_BY), rs.getString(MEDICINE_PHARM_FORM));
        } catch (SQLException e) {
            logger.trace("SQL Exception in method createMedicineFromDB. ", e);
        }
        return medicine;
    }

    /**
     * The method creates a PreparedStatement for create or update a medicine
     *
     * @param statement - Prepared statement
     * @param needId    - boolean variable. Set TRUE if need to write to prepared statement medicine ID
     * @param medicine  - medicine
     */
    private static void updatePrepareStatement(PreparedStatement statement, Medicine medicine, boolean needId) {
        int columnNumber = 1;
        try {
            statement.setString(columnNumber++, medicine.getName());
            statement.setInt(columnNumber++, medicine.getIndivisibleAmount());
            statement.setInt(columnNumber++, medicine.getAmount());
            statement.setString(columnNumber++, medicine.getDosage());
            statement.setString(columnNumber++, medicine.getPharmForm());
            statement.setDate(columnNumber++, new java.sql.Date(medicine.getExpDate().getTime()));
            statement.setBoolean(columnNumber++, medicine.isRecipeRequired());
            statement.setInt(columnNumber++, medicine.getPrice());
            statement.setInt(columnNumber++, medicine.getAddedBy());
            if (needId) {
                statement.setInt(columnNumber, medicine.getId());
            }
        } catch (SQLException e) {
            logger.trace("SQL Exception in method addUpdatePrepareStatement. ", e);
            e.printStackTrace();
        }
    }
}
