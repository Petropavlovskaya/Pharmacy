package by.petropavlovskaja.pharmacy.dao;

import by.petropavlovskaja.pharmacy.dao.sql.OrderSQL;
import by.petropavlovskaja.pharmacy.db.impl.ConnectionPool;
import by.petropavlovskaja.pharmacy.model.Medicine;
import by.petropavlovskaja.pharmacy.model.MedicineInOrder;
import by.petropavlovskaja.pharmacy.model.Order;
import by.petropavlovskaja.pharmacy.model.account.Account;
import by.petropavlovskaja.pharmacy.model.account.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public final class OrderDAO {
    private static Logger logger = LoggerFactory.getLogger(OrderDAO.class);

    private OrderDAO() {
    }

    private static class OrderDAOHolder {
        public static final OrderDAO ORDER_DAO = new OrderDAO();
    }

    public static OrderDAO getInstance() {
        return OrderDAOHolder.ORDER_DAO;
    }

    public List<Order> getByRange(Date firstDate, Date secondDate) {
        return null;
    }

    public void createCart(int accountId) {
        Account account = AccountDAO.getInstance().find(accountId);
        if (account.getId() != -1) {
            int countInsertRowsLogin;
            try (
                    Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                    PreparedStatement statement = conn.prepareStatement(OrderSQL.INSERT_CART.getQuery())
            ) {
                statement.setInt(1, accountId);
                countInsertRowsLogin = statement.executeUpdate();
                if (countInsertRowsLogin != 1) {
                    logger.error("Can't create Cart for login id = " + accountId);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int createMedicineInOrder(int orderId, Medicine medicine, int quantity) {
        int idInsertMedicine = -1;
        int countInsertRowsLogin;
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(OrderSQL.INSERT_MEDICINE_IN_ORDER.getQuery(), Statement.RETURN_GENERATED_KEYS)
        ) {
            statement.setString(1, medicine.getName());
            statement.setString(2, medicine.getDosage());
            statement.setBoolean(3, medicine.isRecipe_required());
            statement.setInt(4, medicine.getIndivisible_amount());
            statement.setInt(5, quantity);
            statement.setInt(6, medicine.getPrice());
            statement.setInt(7, orderId);
            countInsertRowsLogin = statement.executeUpdate();
            if (countInsertRowsLogin != 1) {
                logger.error("Can't add Medicine: " + medicine.toString() + " into table Cart/Order id = " + orderId);
            } else {
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) {
                    idInsertMedicine = resultSet.getInt(1);
                    logger.info("Insert into table Active_med_in_cart complete. Insert Medicine id= " + idInsertMedicine);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idInsertMedicine;
    }

    public boolean updateCart(int cartId, int totalPrice) {
        boolean result = false;
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(OrderSQL.UPDATE_CART.getQuery());
        ) {
            statement.setInt(1, totalPrice);
            statement.setInt(2, cartId);
            int countUpdateRowsMedicine = statement.executeUpdate();
            if (countUpdateRowsMedicine != 1) {
                logger.error("Update into table Order is failed. We update: " + countUpdateRowsMedicine + " rows for cartId: " + cartId);
            } else {
                result = true;
                logger.info("Update into table Order complete. We update data for next cartId: " + cartId);
            }
        } catch (SQLException e) {
            logger.error("SQL Exception in create medicine: " + e);
        } finally {
            return result;
        }
    }

    public void updateMedicinePriceInCart(int cartId, Set<MedicineInOrder> medicineInOrderSet) {
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(OrderSQL.UPDATE_PRICES_IN_CART_BY_ID_AND_CART_ID.getQuery());
        ) {
            for (MedicineInOrder medicineItem : medicineInOrderSet) {
                statement.setInt(1, medicineItem.getPriceForOne());
                statement.setInt(2, medicineItem.getId());
                statement.setInt(3, cartId);
                int countUpdateRowsMedicine = statement.executeUpdate();
                if (countUpdateRowsMedicine != 1) {
                    logger.error("Update into table MedicineInOrder is failed. We update for: " + medicineItem.toString() + " " + countUpdateRowsMedicine + " rows.");
                } else {
                    logger.info("Update into table MedicineInOrder complete. We update next medicine data: " + medicineItem.toString());
                }
            }
        } catch (SQLException e) {
            logger.error("SQL Exception in create medicine: " + e);
        }
    }


    public void createOrder(Customer customer, Order order, Set<MedicineInOrder> medicineInOrderSet) {
/*        boolean resultOrderSet = false;
        int orderId = -1;

        int countInsertRows;
        Savepoint savepoint = null;
        Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
        try {
            conn.setAutoCommit(false);
            savepoint = conn.setSavepoint("start");
            System.out.println("Account autocommit false, savepoint");
        } catch (SQLException e) {
            e.printStackTrace();
        }


        try (
                PreparedStatement psSetOrder = conn.prepareStatement(OrderSQL.INSERT_ORDER.getQuery(), Statement.RETURN_GENERATED_KEYS);
                PreparedStatement psMedicineInOrder = conn.prepareStatement(OrderSQL.INSERT_MEDICINE_IN_ORDER.getQuery())
        ) {
// Create Order
            psSetOrder.setInt(1, customer.getId());
            psSetOrder.setInt(2, order.getOrder_price());
            psSetOrder.setDate(3, new java.sql.Date(new Date().getTime()));
//                     psSetOrder.setDate(3, new java.sql.Date(new Date().getTime()));

            countInsertRows = psSetOrder.executeUpdate();
            if (countInsertRows != 1) {
                conn.rollback();
                logger.error("Insert into table Order is failed. We insert: " + countInsertRows + " rows for Order: " + order.toString());
            } else {
                ResultSet resultSet = psSetOrder.getGeneratedKeys();
                if (resultSet.next()) {
                    orderId = resultSet.getInt(1);
                    conn.commit();
                    logger.info("Insert into table Order complete. Order " + order.toString() + " is added. orderId is: " + orderId);
                }

// Crete MedicineInOrder
                countInsertRows = 0;
                int countMedicine = 0;
                for (MedicineInOrder medicineItem : medicineInOrderSet) {
                    if (medicineItem.getPriceForOne() > 0) {
                        countMedicine++;
                        psMedicineInOrder.setString(1, medicineItem.getMedicine());
                        psMedicineInOrder.setString(2, medicineItem.getDosage());
                        psMedicineInOrder.setBoolean(3, medicineItem.isRecipe_required());
                        psMedicineInOrder.setInt(4, medicineItem.getIndivisible_amount());
                        psMedicineInOrder.setInt(5, medicineItem.getQuantity());
                        psMedicineInOrder.setInt(6, medicineItem.getPriceForOne());
                        psMedicineInOrder.setInt(7, orderId);

                        countInsertRows += psMedicineInOrder.executeUpdate();
                    }
                }

                if (countInsertRows != countMedicine) {
                    conn.rollback(savepoint);
                    logger.error("Insert into table MedicineInOrder is failed. We insert: " + countInsertRows + " but need : " + countMedicine);
                } else {

                    //


                }
*//*
                    conn.commit();
                    if (account.getAccountRole().equals(AccountRole.CUSTOMER)) {
                        OrderDAO.getInstance().createCart(userId);
                        conn.commit();
                        resultAccountSet = true;
                        logger.info("Login: " + login + "CREATE COMPLETE!");
                    } else {
                        conn.rollback(savepoint);
                    }
*//*
            }
        } catch (SQLException e) {
            logger.error("SQL Exception in create account: " + e);
            try {
                conn.rollback(savepoint);
                System.out.println("Login rollback savepoint Exception");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

// Восстановление по умолчанию
        try {
            conn.setAutoCommit(true);
            System.out.println("Account autocommit true");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultAccountSet;
        return false;*/
    }

    public void updateMedicineInCart(int idMedicine, int quantity) {
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(OrderSQL.UPDATE_MEDICINES_QUANTITY_IN_CART_BY_ID.getQuery());
        ) {
            statement.setInt(1, quantity);
            statement.setInt(2, idMedicine);
            int countUpdateRowsMedicine = statement.executeUpdate();
            if (countUpdateRowsMedicine != 1) {
                logger.error("Update into table MedicineInOrder is failed. Row for update id = " + idMedicine + ". Was updated " + countUpdateRowsMedicine + " rows.");
            } else {
                logger.info("Update into table MedicineInOrder complete. We update next medicine id = " + idMedicine);
            }
        } catch (SQLException e) {
            logger.error("SQL Exception in create medicine: " + e);
        }
    }

    public void deleteMedicine(int medicineId) {
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(OrderSQL.DELETE_MEDICINE_BY_ID.getQuery());
        ) {
            statement.setInt(1, medicineId);
            int countUpdateRowsMedicine = statement.executeUpdate();
            if (countUpdateRowsMedicine != 1) {
                logger.error("Delete from table MedicineInOrder is failed. There Was deleted " + countUpdateRowsMedicine + " rows.");
            } else {
                logger.info("Delete from table MedicineInOrder complete. We delete next medicine id = " + medicineId);
            }
        } catch (SQLException e) {
            logger.error("SQL Exception in create medicine: " + e);
        }

    }

    public void createActiveMedicineRelation(int idMedicine, int idMedicineInOrder) {
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(OrderSQL.INSERT_ACTIVE_MEDICINE_IN_CART.getQuery())
        ) {
            statement.setInt(1, idMedicine);
            statement.setInt(2, idMedicineInOrder);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MedicineInOrder findMedicineInOrderByMedicine(Medicine medicine, int orderId) {
        MedicineInOrder resultMedicine = new MedicineInOrder(-1);
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(OrderSQL.FIND_MEDICINE_IN_CART_BY_NAME_DOSAGE_AMOUNT.getQuery());
        ) {
            statement.setString(1, medicine.getName());
            statement.setString(2, medicine.getDosage());
            statement.setInt(3, medicine.getIndivisible_amount());
            statement.setInt(4, orderId);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                resultMedicine = createMedicineInOrderFromDB(rs, false);
            } else {
                System.out.println("Nothing was found ((");
            }

        } catch (SQLException e) {
            logger.error("SQL Exception in create medicine: " + e);
        }
        return resultMedicine;
    }


    public Map<Order, Set<MedicineInOrder>> getAllOrdersWithDetails(int customerId) {
        Set<Order> orders = new TreeSet<>();
        Set<MedicineInOrder> orderDetails = new TreeSet<>();
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(OrderSQL.GET_ALL_ORDERS_WITH_DETAILS.getQuery())
        ) {
            statement.setInt(1, customerId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                orders.add(createOrderFromDB(rs));
                orderDetails.add(createMedicineInOrderFromDB(rs, false));
            }
        } catch (
                SQLException e) {
            System.out.println("Nothing was find ((");
            e.printStackTrace();
        }
        return createOrderWithDetails(orders, orderDetails);
    }

    public Map<Order, Set<MedicineInOrder>> getCustomerOrdersWithDetails(int customerId) {
        Set<Order> orders = new TreeSet<>();
        Set<MedicineInOrder> orderDetails = new TreeSet<>();
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(OrderSQL.FIND_ORDERS_WITH_DETAILS_BY_CUSTOMER_ID.getQuery())
        ) {
            statement.setInt(1, customerId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                orders.add(createOrderFromDB(rs));
                orderDetails.add(createMedicineInOrderFromDB(rs, false));
            }
        } catch (
                SQLException e) {
            System.out.println("Nothing was find ((");
            e.printStackTrace();
        }
        return createOrderWithDetails(orders, orderDetails);
    }

    public Order findCart(int customerId) {
        Order order = new Order(-1);
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(OrderSQL.FIND_CART_BY_CUSTOMER_ID.getQuery())
        ) {
            statement.setInt(1, customerId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                order = createOrderFromDB(rs);
            } else {
                System.out.println("Nothing was found ((");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return order;
    }

    public Set<MedicineInOrder> findMedicineInCartWithActualPrice(int cartId) {
        Comparator<MedicineInOrder> comparator = new MedicineInOrder.NameComparator().thenComparing(
                new MedicineInOrder.DosageComparator().thenComparing(new MedicineInOrder.PriceComparator()));
        Set<MedicineInOrder> cartDetails = new TreeSet<>(comparator);
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(OrderSQL.FIND_ACTUAL_PRICE_FOR_MEDICINES_IN_CART_BY_CART_ID.getQuery());
        ) {
            statement.setInt(1, cartId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                cartDetails.add(createMedicineInOrderFromDB(rs, true));
            }
        } catch (
                SQLException e) {
            System.out.println("Nothing was find ((");
            e.printStackTrace();
        }
        return cartDetails;
    }

    private Order createOrderFromDB(ResultSet rs) {
        Order order = new Order(-1);
        try {
            int order_price = rs.getInt("order_price");
            order = new Order(rs.getInt("order_id"), rs.getInt("fk_customer"),
                    rs.getBoolean("payment_state"), order_price,
                    rs.getTimestamp("order_date"), rs.getBoolean("cart"));
            order.setRub(order_price / 100);
            order.setCoin(order_price % 100);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return order;
    }

    private MedicineInOrder createMedicineInOrderFromDB(ResultSet rs, boolean needTotalAmountFromMedicineList) {
        MedicineInOrder medicineInOrder = new MedicineInOrder(-1);
        try {
            medicineInOrder = new MedicineInOrder(rs.getInt("id"), rs.getString("medicine"),
                    rs.getInt("indivisible_amount"), rs.getString("dosage"), rs.getBoolean("recipe_required"),
                    rs.getInt("quantity"), rs.getInt("price"), rs.getInt("fk_order"));

            if (needTotalAmountFromMedicineList) {
                medicineInOrder.setAmount(rs.getInt("amount"));
            }
            medicineInOrder.setRubCoin();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return medicineInOrder;
    }

    private Map<Order, Set<MedicineInOrder>> createOrderWithDetails(Set<Order> orders, Set<MedicineInOrder> orderDetails) {
        Map<Order, Set<MedicineInOrder>> ordersWithDetails = new TreeMap<>();
        for (Order order : orders) {
            Set<MedicineInOrder> thisOrderDetails = new TreeSet<>();
            for (MedicineInOrder orderDetail : orderDetails) {
                if (order.getId() == orderDetail.getFk_order()) {
                    thisOrderDetails.add(orderDetail);
                }
            }
            ordersWithDetails.put(order, thisOrderDetails);
        }
        return ordersWithDetails;
    }

    private static PreparedStatement prepareStatement(Connection conn, String sql, Object... values) throws
            SQLException {
        PreparedStatement statement = conn.prepareStatement(sql);
        for (int i = 0; i < values.length; i++) {
            statement.setObject(i + 1, values[i]);
        }
        return statement;
    }


}
