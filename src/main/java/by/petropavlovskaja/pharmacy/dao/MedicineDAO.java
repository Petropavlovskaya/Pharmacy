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

/**
 * Class for executing SQL queries to the database related to the medicine
 */
public class MedicineDAO {
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
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                medicineSet.add(new Medicine(rs.getInt("id"), rs.getString("name"),
                        rs.getString("dosage"), rs.getInt("price"), rs.getInt("amount")));
            }
        } catch (
                SQLException e) {
            logger.info("No results were found. ((");
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
            logger.info("No results were found. ((");
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

/*        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery(MedicineSQL.GET_COUNT_MEDICINES.getQuery())
        ) {
            if (rs.next()) {
                count = rs.getInt("count");
            }
        } catch (SQLException e) {
            logger.info("No results were found. ((");
            e.printStackTrace();
        }*/
//        return count;
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
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                medicineList.add(createMedicineFromDB(rs));
            }
        } catch (SQLException e) {
            logger.info("No results were found. ((");
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
                Medicine medicine = new Medicine(rs.getString("name"), rs.getString("dosage"));
                medicineSet.add(medicine);
            }
        } catch (
                SQLException e) {
            logger.info("No results were found. ((");
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
                PreparedStatement statement = addUpdatePrepareStatement(conn, MedicineSQL.INSERT_MEDICINE.getQuery(), medicine)
        ) {
            int countInsertRowsMedicine = statement.executeUpdate();
            if (countInsertRowsMedicine != 1) {
                logger.error("Insert into table Medicine is failed. We insert: " + countInsertRowsMedicine + " rows for medicine: " + medicine.toString());
            } else {
                result = true;
                logger.info("Insert into table Medicine complete. We insert next medicine data: " + medicine.toString());
            }
        } catch (SQLException e) {
            logger.error("SQL Exception in create medicine: " + e);
        }
        return result;
    }

    /**
     * The method updates a medicine in the database
     *
     * @param medicine - a new medicine
     * @return - true if insert was successful
     */
    public boolean update(Medicine medicine) throws IllegalArgumentException {
        boolean result = false;
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = addUpdatePrepareStatement(conn, MedicineSQL.UPDATE_MEDICINE.getQuery(), medicine)
        ) {
            int countUpdateRowsMedicine = statement.executeUpdate();
            if (countUpdateRowsMedicine != 1) {
                logger.error("Update into table Medicine is failed. We update: " + countUpdateRowsMedicine + " rows for medicine: " + medicine.toString());
            } else {
                result = true;
                logger.info("Update into table Medicine complete. We update next medicine data: " + medicine.toString());
            }
        } catch (SQLException e) {
            logger.error("SQL Exception in create medicine: " + e);
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
                logger.error("Delete from table Medicine: " + medicine.toString() + ". We delete: " + countDeleteRowsMedicine + " rows");
            } else {
                result = true;
                logger.info("Pharmacist login = " + pharmacistLogin + " delete Medicine: " + medicine.toString());
            }
        } catch (SQLException e) {
            logger.error("SQL Exception in create medicine: " + e);
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
        return medicineList.size() > 0;
    }

    /**
     * The method finds medicines in the database
     *
     * @param sql    - SQL query
     * @param values - search criteria
     * @return - list of medicines
     */
    private List<Medicine> findBy(String sql, Object... values) {
        List<Medicine> medicine = new ArrayList<>();
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = findPrepareStatement(conn, sql, values);
                ResultSet rs = statement.executeQuery()
        ) {
            while (rs.next()) {
                medicine.add(createMedicineFromDB(rs));
            }
        } catch (SQLException e) {
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

            medicine = new Medicine(rs.getInt("id"), rs.getString("name"),
                    rs.getInt("indivisible_amount"), rs.getInt("amount"),
                    rs.getString("dosage"), rs.getDate("exp_date"),
                    rs.getBoolean("recipe_required"), rs.getInt("price"),
                    rs.getInt("added_by"), rs.getString("pharm_form"));
        } catch (SQLException e) {
            logger.error("Can't create Medicine entity from DB. " + e);
        }
        return medicine;
    }

    /**
     * The method creates a PreparedStatement from a variable number of parameters
     *
     * @param conn   - Connection
     * @param sql    - SQL query
     * @param values - parameters
     * @return - PreparedStatement
     */
    private static PreparedStatement findPrepareStatement(Connection conn, String sql, Object... values) throws
            SQLException {
        PreparedStatement statement = conn.prepareStatement(sql);
        for (int i = 0; i < values.length; i++) {
            statement.setObject(i + 1, values[i]);
        }
        return statement;
    }

    /**
     * The method creates a PreparedStatement for create or update a medicine
     *
     * @param conn     - Connection
     * @param sql      - SQL query
     * @param medicine - medicine
     * @return - PreparedStatement
     */
    private static PreparedStatement addUpdatePrepareStatement(Connection conn, String sql, Medicine medicine) {
        PreparedStatement statement = null;
        try {
            statement = conn.prepareStatement(sql);
            statement.setString(1, medicine.getName());
            statement.setInt(2, medicine.getIndivisibleAmount());
            statement.setInt(3, medicine.getAmount());
            statement.setString(4, medicine.getDosage());
            statement.setString(5, medicine.getPharmForm());
            statement.setDate(6, new java.sql.Date(medicine.getExpDate().getTime()));
            statement.setBoolean(7, medicine.isRecipeRequired());
            statement.setInt(8, medicine.getPrice());
            statement.setInt(9, medicine.getAddedBy());
            if (sql.equalsIgnoreCase(MedicineSQL.UPDATE_MEDICINE.getQuery())) {
                statement.setInt(10, medicine.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statement;
    }
}
