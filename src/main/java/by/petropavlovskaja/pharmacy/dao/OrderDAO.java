package by.petropavlovskaja.pharmacy.dao;

import by.petropavlovskaja.pharmacy.dao.sql.AccountSQL;
import by.petropavlovskaja.pharmacy.dao.sql.MedicineInOrderSQL;
import by.petropavlovskaja.pharmacy.dao.sql.MedicineSQL;
import by.petropavlovskaja.pharmacy.dao.sql.OrderSQL;
import by.petropavlovskaja.pharmacy.dao.sql.RecipeSQL;
import by.petropavlovskaja.pharmacy.db.ConnectionPool;
import by.petropavlovskaja.pharmacy.model.Medicine;
import by.petropavlovskaja.pharmacy.model.MedicineInOrder;
import by.petropavlovskaja.pharmacy.model.Order;
import by.petropavlovskaja.pharmacy.model.Recipe;
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
import java.util.*;

import static by.petropavlovskaja.pharmacy.dao.DatabaseColumnNameConstant.*;


/**
 * Class for executing SQL queries to the database related to the order
 */
public class OrderDAO {
    private static Logger logger = LoggerFactory.getLogger(OrderDAO.class);
    private static RecipeDAO recipeDAO = RecipeDAO.getInstance();
    private static MedicineDAO medicineDAO = MedicineDAO.getInstance();
    private static MedicineInOrderDAO medicineInOrderDAO = MedicineInOrderDAO.getInstance();

    /**
     * String property for logger message
     */
    private String loggerMessage;

    /**
     * Constructor - create INSTANCE of class
     */
    private OrderDAO() {
    }

    /**
     * Nested class create instance of the class
     */
    private static class OrderDAOHolder {
        public static final OrderDAO ORDER_DAO = new OrderDAO();
    }

    /**
     * The method for get instance of the class
     *
     * @return - class instance
     */
    public static OrderDAO getInstance() {
        return OrderDAOHolder.ORDER_DAO;
    }

    /**
     * The method inserts an order as cart into the database
     *
     * @param accountId - account ID
     */
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
                    loggerMessage = "Can't create Cart for login id = " + accountId;
                    logger.error(loggerMessage);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The method updates in the database a total price of medicines
     *
     * @param cartId     - cart ID
     * @param totalPrice - total price of medicines
     */
    public void updateCartPrice(int cartId, int totalPrice) {
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(OrderSQL.UPDATE_CART.getQuery())
        ) {
            statement.setInt(1, totalPrice);
            statement.setInt(2, cartId);
            int countUpdateRowsMedicine = statement.executeUpdate();
            if (countUpdateRowsMedicine != 1) {
                loggerMessage = "Update into table Order is failed. We update: " + countUpdateRowsMedicine + " rows for cartId: " + cartId;
                logger.error(loggerMessage);
            } else {
                loggerMessage = "Update into table Order complete. We update data for next cartId: " + cartId;
                logger.info(loggerMessage);
            }
        } catch (SQLException e) {
            loggerMessage = "SQL Exception in method updateCart. Cart id = " + cartId + ", " + " total price = " + totalPrice + ". ";
            logger.error(loggerMessage, e);
        }
    }

    /**
     * The method creates record and modify tables in the database after purchasing medicines.
     * It uses savepoint statement and consist methods: {@link OrderDAO#setOrder(Savepoint, Connection, PreparedStatement, Customer, Order)},
     * {@link OrderDAO#moveMedicineFromCartToOrder(Savepoint, Connection, PreparedStatement, int, Set)},
     * {@link OrderDAO#tieRecipe(Savepoint, Connection, PreparedStatement, int, Set, Set)},
     * {@link OrderDAO#getMedicineUpdateInfo(Set, int)}, {@link OrderDAO#updateMedicineAfterBuy(Savepoint, Connection, PreparedStatement, Set)},
     * {@link OrderDAO#updateAccountBalance(Savepoint, Connection, PreparedStatement, int, int)},
     * {@link OrderDAO#updateCartAfterBuy(Savepoint, Connection, PreparedStatement, Set)}
     *
     * @param customer           - customer
     * @param order              - order
     * @param medicineInOrderSet - set of medicines in the order
     * @return true if total update was successful
     */
    public boolean createOrder(Customer customer, Order order, Set<MedicineInOrder> medicineInOrderSet) {
        Set<MedicineInOrder> medicineForBuy = getMedicineForBuy(medicineInOrderSet);
        Set<Recipe> availableRecipes = recipeDAO.getAllValidRecipe(customer.getId());
        boolean totalUpdate = false;
        int orderId;

        Savepoint savepoint = null;
        Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();

        try (
                PreparedStatement psOrder = conn.prepareStatement(OrderSQL.INSERT_ORDER.getQuery(), Statement.RETURN_GENERATED_KEYS);
                PreparedStatement psMedicineInOrder = conn.prepareStatement(MedicineInOrderSQL.INSERT_MEDICINE_IN_ORDER.getQuery());
                PreparedStatement psRecipe = conn.prepareStatement(RecipeSQL.UPDATE_ID_MEDICINE_IN_ORDER.getQuery());
                PreparedStatement psMedicine = conn.prepareStatement(MedicineSQL.UPDATE_MEDICINE_AFTER_BUY.getQuery());
                PreparedStatement psAccount = conn.prepareStatement(AccountSQL.UPDATE_BALANCE.getQuery());
                PreparedStatement psUpdateCart = conn.prepareStatement(MedicineInOrderSQL.DELETE_MEDICINE_BY_ID.getQuery())
        ) {
            conn.setAutoCommit(false);
            savepoint = conn.setSavepoint();

// 1
// Create Order
            orderId = setOrder(savepoint, conn, psOrder, customer, order);
            if (orderId == -1) {
                conn.rollback();
                logger.trace("Rollback to savepoint in metrod createOrder. OrderId == -1");
            } else {
// 2
// Move medicineInOrder from Cart to Order details
                boolean medicineInOrderUpdate = moveMedicineFromCartToOrder(savepoint, conn, psMedicineInOrder, orderId, medicineForBuy);
// 3
// Snap Recipe to Order
                if (medicineInOrderUpdate) {
                    boolean recipeUpdate = tieRecipe(savepoint, conn, psRecipe, orderId, medicineForBuy, availableRecipes);
// 4
// Update count of medicine
                    if (recipeUpdate) {
                        Set<Medicine> realMedicineSet = getMedicineUpdateInfo(medicineForBuy, order.getId());
                        boolean medicineUpdate = updateMedicineAfterBuy(savepoint, conn, psMedicine, realMedicineSet);
// 5
// Update account balance
                        if (medicineUpdate) {
                            int balance = customer.getBalance();
                            int newBalance = balance - order.getOrderPrice();
                            boolean balanceUpdate = updateAccountBalance(savepoint, conn, psAccount, customer.getId(), newBalance);
// 6
// Update info in Cart
                            if (balanceUpdate) {
                                boolean isCartUpdate = updateCartAfterBuy(savepoint, conn, psUpdateCart, medicineForBuy);
                                if (isCartUpdate) {
                                    conn.commit();
                                    totalUpdate = true;
                                    logger.info("Delete bought items from cart was successful.");
                                } else {
                                    conn.rollback(savepoint);
                                    logger.error("Delete bought items from cart was failed.");
                                }
                            }
                        }
                    }
                }
            }
// Восстановление по умолчанию
            conn.setAutoCommit(true);
            conn.close();
        } catch (
                SQLException e) {
            logger.trace("SQL Exception in create account: ", e);
            try {
                conn.rollback(savepoint);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return totalUpdate;
    }

    /**
     * The method updates the curt after purchasing medicines {@link OrderDAO#createOrder(Customer, Order, Set)}
     *
     * @param conn           - connection to the database
     * @param savepoint      - save point statement
     * @param medicineForBuy - set of medicines for purchasing
     * @param psUpdateCart   - prepared statement for execute SQL query
     * @return true if update was successful
     */
    // 5 Update Cart after buy
    private boolean updateCartAfterBuy(Savepoint savepoint, Connection conn, PreparedStatement psUpdateCart,
                                       Set<MedicineInOrder> medicineForBuy) {
        boolean result = false;
        int countNeedUpdate = medicineForBuy.size();
        int countUpdate = 0;
        try {
            List<MedicineInOrder> orderMedicine = new ArrayList<>(medicineForBuy);
            for (MedicineInOrder medicineInOrder : orderMedicine) {
                psUpdateCart.setInt(1, medicineInOrder.getId());
                countUpdate += psUpdateCart.executeUpdate();
            }

            if (countNeedUpdate != countUpdate) {
                conn.rollback(savepoint);
                loggerMessage = "Delete from table MedicineInOrder is failed. There Was deleted " + countUpdate
                        + " rows. But was expected " + countNeedUpdate;
                logger.trace(loggerMessage);
            } else {
                result = true;
                loggerMessage = "Delete from table MedicineInOrder complete. We delete " + countUpdate + " medicines.";
                logger.info(loggerMessage);
            }
        } catch (SQLException e) {
            try {
                conn.rollback(savepoint);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            logger.trace("SQL Exception in delete medicine. ", e);
            e.printStackTrace();
        }

        return result;
    }

    /**
     * The method of getting the available medicines for purchasing
     *
     * @param medicineInOrderSet - set of medicines in the cart
     * @return - set of medicines available for purchasing
     */
    private Set<MedicineInOrder> getMedicineForBuy(Set<MedicineInOrder> medicineInOrderSet) {
        Set<MedicineInOrder> medicineForBuy = new HashSet<>();
        for (MedicineInOrder medicineItem : medicineInOrderSet) {
            if (medicineItem.getPriceForOne() > 0) {
                medicineForBuy.add(medicineItem);
            }
        }
        return medicineForBuy;
    }

    /**
     * The method updates the curt after purchasing medicines {@link OrderDAO#createOrder(Customer, Order, Set)}
     *
     * @param conn       - connection to the database
     * @param savepoint  - save point statement
     * @param psSetOrder - prepared statement for execute SQL query
     * @param customer   - customer instance
     * @param order      - order instance
     * @return ID record of created order if update was successful or ID = -1 if wasn't
     */
    // 1 Set Order
    private int setOrder(Savepoint savepoint, Connection conn, PreparedStatement psSetOrder, Customer customer, Order order) {
        int orderId = -1;
        int updateRow;
        try {
            psSetOrder.setInt(1, customer.getId());
            psSetOrder.setInt(2, order.getOrderPrice());
            psSetOrder.setDate(3, new java.sql.Date(new Date().getTime()));

            updateRow = psSetOrder.executeUpdate();
            if (updateRow != 1) {
                conn.rollback(savepoint);
                loggerMessage = "Insert into table Order is failed. We insert: " + updateRow + " rows for Order: " + order.toString();
                logger.trace(loggerMessage);
            } else {
                ResultSet resultSet = psSetOrder.getGeneratedKeys();
                if (resultSet.next()) {
                    orderId = resultSet.getInt(1);
                    loggerMessage = "Insert into table Order complete. Order " + order.toString() + " is added. orderId is: " + orderId;
                    logger.info(loggerMessage);
                }
            }
        } catch (SQLException e) {
            try {
                conn.rollback(savepoint);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
        return orderId;
    }

    /**
     * The method moves available for purchasing medicines from the curt to created record of order {@link OrderDAO#createOrder(Customer, Order, Set)}
     *
     * @param conn              - connection to the database
     * @param savepoint         - save point statement
     * @param psMedicineInOrder - prepared statement for execute SQL query
     * @param medicineForBuy    - set of medicines
     * @param orderId           - order ID
     * @return true if update was successful
     */
    // 2 Set MIO
    private boolean moveMedicineFromCartToOrder(Savepoint savepoint, Connection conn, PreparedStatement psMedicineInOrder,
                                                int orderId, Set<MedicineInOrder> medicineForBuy) {
        boolean resultUpdateMedicineInOrder = false;
        int updateRow = 0;
        int countMedicine = 0;
        try {
            for (MedicineInOrder medicineItem : medicineForBuy) {
                countMedicine++;
                int columnNumber = 1;
                psMedicineInOrder.setString(columnNumber++, medicineItem.getMedicine());
                psMedicineInOrder.setString(columnNumber++, medicineItem.getDosage());
                psMedicineInOrder.setDate(columnNumber++, new java.sql.Date(medicineItem.getExpDate().getTime()));
                psMedicineInOrder.setBoolean(columnNumber++, medicineItem.isRecipeRequired());
                psMedicineInOrder.setInt(columnNumber++, medicineItem.getIndivisibleAmount());
                psMedicineInOrder.setInt(columnNumber++, medicineItem.getQuantity());
                psMedicineInOrder.setInt(columnNumber++, medicineItem.getPriceForOne());
                psMedicineInOrder.setInt(columnNumber, orderId);
                updateRow += psMedicineInOrder.executeUpdate();
            }
            if (updateRow != countMedicine) {
                conn.rollback(savepoint);
                loggerMessage = "Error to move MedicineInOrder from Cart to Order . We insert: " + updateRow + " but was expected " + countMedicine;
                logger.trace(loggerMessage);
            } else {
                resultUpdateMedicineInOrder = true;
                loggerMessage = "Move MedicineInOrder from Cart to Order complete. Moved MedicineInOrder: " + medicineForBuy.toString();
                logger.info(loggerMessage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultUpdateMedicineInOrder;
    }

    /**
     * The method ties a recipe to a purchased medicine {@link OrderDAO#createOrder(Customer, Order, Set)}
     *
     * @param conn           - connection to the database
     * @param savepoint      - save point statement
     * @param psRecipe       - prepared statement for execute SQL query
     * @param medicineForBuy - set of medicines
     * @param orderId        - order ID
     * @param recipes        - set of recipes
     * @return true if update was successful
     */
    // 3 Set Recipe
    private boolean tieRecipe(Savepoint savepoint, Connection conn, PreparedStatement psRecipe, int orderId,
                              Set<MedicineInOrder> medicineForBuy, Set<Recipe> recipes) {
        boolean resultUpdateRecipe = false;
        int updateRow = 0;
        int countMedicineReqRecipe = 0;
        int countUpdatedRecipe = 0;
        try {
            Set<Recipe> recipesForUpdate = new HashSet<>();
            for (MedicineInOrder medicineItem : medicineForBuy) {
                if (medicineItem.isRecipeRequired()) {
                    countMedicineReqRecipe++;
                    for (Recipe recipeItem : recipes) {
                        if ((recipeItem.getMedicine().equals(medicineItem.getMedicine()) &&
                                recipeItem.getDosage().equals(medicineItem.getDosage()))) {
                            recipeItem.setIdMedicineInOrder(orderId);
                            recipesForUpdate.add(recipeItem);
                            countUpdatedRecipe++;
                            break;
                        }
                    }
                }
            }
            if (countMedicineReqRecipe == countUpdatedRecipe) {
                for (Recipe recipeItem : recipesForUpdate) {
                    psRecipe.setInt(1, orderId);
                    psRecipe.setInt(2, recipeItem.getId());
                    updateRow += psRecipe.executeUpdate();
                }
                if (countUpdatedRecipe == updateRow) {
                    resultUpdateRecipe = true;
                    loggerMessage = "Set orderId in Recipe complete. Was updated " + updateRow + " rows for orderId=" + orderId;
                    logger.info(loggerMessage);
                } else {
                    loggerMessage = "Error count updated Recipes in DB = " + countUpdatedRecipe + ", but was expected " + countMedicineReqRecipe;
                    logger.trace(loggerMessage);
                    conn.rollback(savepoint);
                }
            } else {
                conn.rollback(savepoint);
                loggerMessage = "Error set orderId in Recipe. We get to update " + countUpdatedRecipe + " recipes, but was expected " + countMedicineReqRecipe;
                logger.trace(loggerMessage);
            }
        } catch (SQLException e) {
            try {
                conn.rollback(savepoint);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
        return resultUpdateRecipe;
    }

    /**
     * The method of getting a purchased medicines to update the available amount of medicines
     *
     * @param medicineInOrderSet - set of medicines
     * @param orderId            - order ID
     * @return set of medicines
     */
    // 4.1
    private Set<Medicine> getMedicineUpdateInfo(Set<MedicineInOrder> medicineInOrderSet, int orderId) {
        Set<Medicine> realMedicineSet = medicineDAO.getMedicineDataForChangeAmount(orderId);
        for (Medicine realMedicine : realMedicineSet) {
            for (MedicineInOrder medicineInOrder : medicineInOrderSet) {
                if (medicineEquals(realMedicine, medicineInOrder)) {
                    int newAmount = realMedicine.getAmount() - medicineInOrder.getQuantity();
                    realMedicine.setAmount(newAmount);
                    break;
                }
            }
        }
        return realMedicineSet;
    }

    /**
     * The method equals the medicine in the database with the medicine in the order {@link OrderDAO#createOrder(Customer, Order, Set)}
     *
     * @param realMedicine    - a medicine in the database
     * @param medicineInOrder - a medicine in the order
     * @return true if medicines are the same
     */
    private boolean medicineEquals(Medicine realMedicine, MedicineInOrder medicineInOrder) {
        boolean name = realMedicine.getName().equals(medicineInOrder.getMedicine());
        boolean dosage = realMedicine.getDosage().equals(medicineInOrder.getDosage());
        boolean price = realMedicine.getPrice() == medicineInOrder.getPriceForOne();
        return name && dosage && price;
    }

    /**
     * The method updates medicines available amount after purchased {@link OrderDAO#createOrder(Customer, Order, Set)}
     *
     * @param conn            - connection to the database
     * @param savepoint       - save point statement
     * @param psMedicine      - prepared statement for execute SQL query
     * @param realMedicineSet - purchased set of medicines
     * @return true if update was successful
     */
    // 4.2
    private boolean updateMedicineAfterBuy(Savepoint savepoint, Connection conn, PreparedStatement psMedicine, Set<Medicine> realMedicineSet) {
        boolean resultUpdate = false;
        int countUpdateRowsMedicine = 0;
        try {
            for (Medicine medicineItem : realMedicineSet) {
                psMedicine.setInt(1, medicineItem.getAmount());
                psMedicine.setInt(2, medicineItem.getId());
                countUpdateRowsMedicine += psMedicine.executeUpdate();
            }
            if (countUpdateRowsMedicine != realMedicineSet.size()) {
                conn.rollback(savepoint);
                loggerMessage = "Update into table Medicine is failed. It was updated " + countUpdateRowsMedicine
                        + ". Was updated " + realMedicineSet.size() + " rows.";
                logger.trace(loggerMessage);
            } else {
                resultUpdate = true;
                logger.info("Update into table Medicine complete.");
            }
        } catch (SQLException e) {
            try {
                conn.rollback(savepoint);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            logger.error("SQL Exception in update amount medicine: ", e);
        }
        return resultUpdate;
    }

    /**
     * The method updates customer balance after purchased {@link OrderDAO#createOrder(Customer, Order, Set)}
     *
     * @param conn       - connection to the database
     * @param savepoint  - save point statement
     * @param psAccount  - prepared statement for execute SQL query
     * @param customerId - customer ID
     * @param balance    - balance
     * @return true if update was successful
     */
    // 5
    public boolean updateAccountBalance(Savepoint savepoint, Connection conn, PreparedStatement psAccount, int customerId, int balance) {
        boolean result = false;
        try {
            psAccount.setInt(1, balance);
            psAccount.setInt(2, customerId);
            int updateRow = psAccount.executeUpdate();
            if (updateRow != 1) {
                conn.rollback(savepoint);
                loggerMessage = "Update in table Account is failed. We update: " + updateRow + " rows for accountId: " + customerId;
                logger.trace(loggerMessage);
            } else {
                result = true;
                loggerMessage = "Update in table Account complete. For account id = " + customerId + " was set balance: " + balance;
                logger.info(loggerMessage);
            }
        } catch (SQLException e) {
            try {
                conn.rollback(savepoint);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
        return result;
    }

    /**
     * The method inserts a relation between the medicines in the cart and in the medicines set
     *
     * @param idMedicine        - medicine ID
     * @param idMedicineInOrder - medicine in the cart ID
     */
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

    /**
     * The method finds all customer's orders (except cart) with medicines details
     *
     * @param customerId - customer ID
     * @return - orders with medicines details
     */
    public Map<Order, Set<MedicineInOrder>> getAllOrdersWithDetails(int customerId) {
        Comparator<Order> orderComparator = new Order.OrderIdComparator();
        Set<Order> orders = new TreeSet<>(orderComparator);
        Comparator<MedicineInOrder> medicineInOrderComparator = new MedicineInOrder.IdComparator();
        Set<MedicineInOrder> orderDetails = new TreeSet<>(medicineInOrderComparator);
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(OrderSQL.GET_ALL_ORDERS_WITH_DETAILS.getQuery())
        ) {
            statement.setInt(1, customerId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    orders.add(createOrderFromDB(rs));
                    orderDetails.add(medicineInOrderDAO.createMedicineInOrderFromDB(rs, false));
                }
            }
        } catch (
                SQLException e) {
            logger.trace("SQL exception in method getAllOrdersWithDetails. ", e);
            e.printStackTrace();
        }
        return createOrderWithDetails(orders, orderDetails);
    }

    /**
     * The method finds customer's cart
     *
     * @param customerId - customer ID
     * @return - cart
     */
    public Order findCart(int customerId) {
        Order order = new Order(-1);
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(OrderSQL.FIND_CART_BY_CUSTOMER_ID.getQuery())
        ) {
            statement.setInt(1, customerId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    order = createOrderFromDB(rs);
                }
            }
        } catch (SQLException e) {
            logger.trace("SQL exception in method findCart. ", e);
            e.printStackTrace();
        }
        return order;
    }

    /**
     * The method creates order instance from ResultSet
     *
     * @param rs - ResultSet
     * @return - Order instance if account was found or Order with id = -1 if wasn't
     */
    private Order createOrderFromDB(ResultSet rs) {
        Order order = new Order(-1);
        try {
            int order_price = rs.getInt(ORDER_PRICE);
            order = new Order(rs.getInt(ORDER_ID), rs.getInt(ORDER_CUSTOMER_KEY),
                    order_price, rs.getTimestamp(ORDER_DATE), rs.getBoolean(ORDER_CART));
            order.setRub(order_price / 100);
            order.setCoin(order_price % 100);
        } catch (SQLException e) {
            logger.trace("SQL exception in method createOrderFromDB. ", e);
            e.printStackTrace();
        }
        return order;
    }

    /**
     * The method creates map of orders and medicines set in them
     *
     * @param orders       - orders
     * @param orderDetails - medicines set in the orders
     * @return - map of orders and medicines set in them
     */
    private Map<Order, Set<MedicineInOrder>> createOrderWithDetails
    (Set<Order> orders, Set<MedicineInOrder> orderDetails) {
        Comparator<Order> orderComparator = new Order.OrderIdComparator();
        Comparator<MedicineInOrder> medicineInOrderComparator = new MedicineInOrder.NameComparator()
                .thenComparing(new MedicineInOrder.DosageComparator().thenComparing(new MedicineInOrder.PriceComparator()));
        Map<Order, Set<MedicineInOrder>> ordersWithDetails = new TreeMap<>(orderComparator);
        for (Order order : orders) {
            Set<MedicineInOrder> thisOrderDetails = new TreeSet<>(medicineInOrderComparator);
            for (MedicineInOrder orderDetail : orderDetails) {
                if (order.getId() == orderDetail.getFkOrder()) {
                    thisOrderDetails.add(orderDetail);
                }
            }
            ordersWithDetails.put(order, thisOrderDetails);
        }
        return ordersWithDetails;
    }

    /**
     * The method deletes an order and medicines in it (method uses for delete information from "order history")
     *
     * @param orderId - an order ID
     * @return - true if order was delete successful
     */
    public boolean deleteOrder(int orderId) {
        boolean resultDelete = false;
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(OrderSQL.DELETE_ORDER.getQuery())
        ) {
            statement.setInt(1, orderId);
            int countUpdateRowsMedicine = statement.executeUpdate();
            if (countUpdateRowsMedicine != 1) {
                loggerMessage = "Update table Order is failed. We delete for orderId: " + orderId + " " + countUpdateRowsMedicine + " rows.";
                logger.trace(loggerMessage);
            } else {
                resultDelete = true;
                loggerMessage = "Update table Order complete. We delete next orderId: " + orderId;
                logger.info(loggerMessage);
            }
        } catch (SQLException e) {
            logger.trace("SQL exception in method deleteOrder. ", e);
            e.printStackTrace();
        }
        return resultDelete;
    }
}
