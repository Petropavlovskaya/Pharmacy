package by.petropavlovskaja.pharmacy.dao;

import by.petropavlovskaja.pharmacy.dao.sql.MedicineSQL;
import by.petropavlovskaja.pharmacy.db.impl.ConnectionPool;
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

public final class MedicineDAO {
    private static Logger logger = LoggerFactory.getLogger(MedicineDAO.class);

    private MedicineDAO() {
    }

    private static class MedicineDAOHolder {
        public static final MedicineDAO MEDICINE_DAO = new MedicineDAO();
    }

    public static MedicineDAO getInstance() {
        return MedicineDAOHolder.MEDICINE_DAO;
    }

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
            System.out.println("Nothing was find ((");
            e.printStackTrace();
        }
        return medicineSet;
    }

    public Medicine findById(Integer medicineId) {
        List<Medicine> medicineList = findBy(MedicineSQL.FIND_MEDICINES_BY_ID.getQuery(), medicineId);
        System.out.println(medicineList.toString());
        if (medicineList.size() > 1) {
            return new Medicine(-1);
        }
        return medicineList.get(0);
    }

    public Set<Medicine> getAll() {
        Comparator<Medicine> comp = new Medicine.MedicineNameComparator().thenComparing(new Medicine.MedicineDosageComparator())
                .thenComparing(new Medicine.MedicineDateComparator());
        Set<Medicine> medicineSet = new TreeSet<>(comp);
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery(MedicineSQL.GET_ALL_MEDICINES.getQuery())
        ) {
            while (rs.next()) {
                medicineSet.add(createMedicineFromDB(rs));
            }
        } catch (
                SQLException e) {
            System.out.println("Nothing was find ((");
            e.printStackTrace();
        }
        return medicineSet;
    }

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
            System.out.println("Nothing was find ((");
            e.printStackTrace();
        }
        return medicineSet;
    }

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

    private static PreparedStatement findPrepareStatement(Connection conn, String sql, Object... values) throws
            SQLException {
        PreparedStatement statement = conn.prepareStatement(sql);
        for (int i = 0; i < values.length; i++) {
            statement.setObject(i + 1, values[i]);
        }
        return statement;
    }

    private static PreparedStatement addUpdatePrepareStatement(Connection conn, String sql, Medicine medicine) {
        PreparedStatement statement = null;
        try {
            statement = conn.prepareStatement(sql);
            statement.setString(1, medicine.getName());
            statement.setInt(2, medicine.getIndivisible_amount());
            statement.setInt(3, medicine.getAmount());
            statement.setString(4, medicine.getDosage());
            statement.setString(5, medicine.getPharm_form());
            statement.setDate(6, new java.sql.Date(medicine.getExp_date().getTime()));
            statement.setBoolean(7, medicine.isRecipe_required());
            statement.setInt(8, medicine.getPrice());
            statement.setInt(9, medicine.getAdded_by());
            if (sql.equalsIgnoreCase(MedicineSQL.UPDATE_MEDICINE.getQuery())) {
                statement.setInt(10, medicine.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statement;
    }
}
