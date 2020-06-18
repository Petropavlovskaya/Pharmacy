package by.petropavlovskaja.pharmacy.dao;

import by.petropavlovskaja.pharmacy.dao.sql.AccountSQL;
import by.petropavlovskaja.pharmacy.dao.sql.MedicineSQL;
import by.petropavlovskaja.pharmacy.dao.sql.OrderSQL;
import by.petropavlovskaja.pharmacy.dao.sql.RecipeSQL;
import by.petropavlovskaja.pharmacy.db.impl.ConnectionPool;
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

public final class OrderDAO {
    private static Logger logger = LoggerFactory.getLogger(OrderDAO.class);
    private static RecipeDAO recipeDAO = RecipeDAO.getInstance();
    private static MedicineDAO medicineDAO = MedicineDAO.getInstance();

    private OrderDAO() {
    }

    private static class OrderDAOHolder {
        public static final OrderDAO ORDER_DAO = new OrderDAO();
    }

    public static OrderDAO getInstance() {
        return OrderDAOHolder.ORDER_DAO;
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

    public void updateCartPrice(int cartId, int totalPrice) {
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(OrderSQL.UPDATE_CART.getQuery())
        ) {
            statement.setInt(1, totalPrice);
            statement.setInt(2, cartId);
            int countUpdateRowsMedicine = statement.executeUpdate();
            if (countUpdateRowsMedicine != 1) {
                logger.error("Update into table Order is failed. We update: " + countUpdateRowsMedicine + " rows for cartId: " + cartId);
            } else {
                logger.info("Update into table Order complete. We update data for next cartId: " + cartId);
            }
        } catch (SQLException e) {
            logger.error("SQL Exception in method updateCart. Cart id = " + cartId + ", " + " total price = " + totalPrice + ". " + e);
        }
    }

    public void updateMedicinePriceInCart(int cartId, Set<MedicineInOrder> medicineInOrderSet) {
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(OrderSQL.UPDATE_PRICES_IN_CART_BY_ID_AND_CART_ID.getQuery())
        ) {
            for (MedicineInOrder medicineItem : medicineInOrderSet) {
                statement.setInt(1, medicineItem.getPriceForOne());
                statement.setInt(2, medicineItem.getId());
//                statement.setInt(3, cartId);
                int countUpdateRowsMedicine = statement.executeUpdate();
                if (countUpdateRowsMedicine != 1) {
                    logger.error("Update into table MedicineInOrder is failed. We update for: " + medicineItem.toString() + " " + countUpdateRowsMedicine + " rows.");
                } else {
                    logger.info("Update into table MedicineInOrder complete. We update next medicine data: " + medicineItem.toString());
                }
            }
        } catch (SQLException e) {
            logger.error("SQL Exception in method updateMedicinePriceInCart. Cart id = " + cartId + ". " + e);
        }
    }

    public boolean createOrder(Customer customer, Order order, Set<MedicineInOrder> medicineInOrderSet) {
        Set<MedicineInOrder> medicineForBuy = getMedicineForBuy(medicineInOrderSet);
        Set<Recipe> availableRecipes = recipeDAO.getAllValidRecipe(customer.getId());
        boolean totalUpdate = false;
        int orderId;

        Savepoint savepoint = null;
        Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();

        try (
                PreparedStatement psOrder = conn.prepareStatement(OrderSQL.INSERT_ORDER.getQuery(), Statement.RETURN_GENERATED_KEYS);
                PreparedStatement psMedicineInOrder = conn.prepareStatement(OrderSQL.INSERT_MEDICINE_IN_ORDER.getQuery());
                PreparedStatement psRecipe = conn.prepareStatement(RecipeSQL.UPDATE_ID_MEDICINE_IN_ORDER.getQuery());
                PreparedStatement psMedicine = conn.prepareStatement(MedicineSQL.UPDATE_MEDICINE_AFTER_BUY.getQuery());
                PreparedStatement psAccount = conn.prepareStatement(AccountSQL.UPDATE_BALANCE.getQuery());
                PreparedStatement psUpdateCart = conn.prepareStatement(OrderSQL.DELETE_MEDICINE_BY_ID.getQuery())
        ) {
            conn.setAutoCommit(false);
            savepoint = conn.setSavepoint();

// 1
// Create Order
            orderId = setOrder(savepoint, conn, psOrder, customer, order);
            if (orderId == -1) {
                conn.rollback(savepoint);
                System.out.println("Login rollback savepoint Exception. Order Id = -1");
            } else {
// 2
// Move medicineInOrder from Cart to Order details
                boolean isMedicineInOrderUpdate = moveMedicineFromCartToOrder(savepoint, conn, psMedicineInOrder, orderId, medicineForBuy);
// 3
// Snap Recipe to Order
                if (isMedicineInOrderUpdate) {
                    boolean isRecipeUpdate = snapRecipe(savepoint, conn, psRecipe, orderId, medicineForBuy, availableRecipes);
// 4
// Update count of medicine
                    if (isRecipeUpdate) {
                        Set<Medicine> realMedicineSet = getMedicineUpdateInfo(medicineForBuy, order.getId());
                        boolean isMedicineUpdate = updateMedicineAfterBuy(savepoint, conn, psMedicine, realMedicineSet);
// 5
// Update account balance
                        if (isMedicineUpdate) {
                            int balance = customer.getBalance();
                            int newBalance = balance - order.getOrder_price();
                            boolean isAccountUpdate = updateAccountBalance(savepoint, conn, psAccount, customer.getId(), newBalance);
                            if (isAccountUpdate) {
// 6
// Update info in Cart
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
        } catch (
                SQLException e) {
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
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
        return totalUpdate;
    }

    // 5 Update Cart after buy
    private boolean updateCartAfterBuy(Savepoint savepoint, Connection conn, PreparedStatement psUpdateCart,
                                       Set<MedicineInOrder> medicineForBuy) {
        boolean result = false;
        int countNeedUpdate = medicineForBuy.size();
        int countUpdate = 0;
        try {
            List<MedicineInOrder> orderMedicine = new ArrayList<>();
            orderMedicine.addAll(medicineForBuy);
            for (int i = 0; i < orderMedicine.size(); i++) {
                psUpdateCart.setInt(1, orderMedicine.get(i).getId());
                countUpdate += psUpdateCart.executeUpdate();
            }
            if (countNeedUpdate != countUpdate) {
                conn.rollback(savepoint);
                logger.error("Delete from table MedicineInOrder is failed. There Was deleted " + countUpdate
                        + " rows. Was expected to delete " + countNeedUpdate + " rows.");
            } else {
                result = true;
                logger.info("Delete from table MedicineInOrder complete. We delete " + countUpdate + " medicines.");
            }
        } catch (SQLException e) {
            try {
                conn.rollback(savepoint);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            logger.error("SQL Exception in delete medicine. " + e);
            e.printStackTrace();
        }

        return result;
    }

    private Set<MedicineInOrder> getMedicineForBuy(Set<MedicineInOrder> medicineInOrderSet) {
        Set<MedicineInOrder> medicineForBuy = new HashSet<>();
        for (MedicineInOrder medicineItem : medicineInOrderSet) {
            if (medicineItem.getPriceForOne() > 0) {
                medicineForBuy.add(medicineItem);
            }
        }
        return medicineForBuy;
    }

    // 1 Set Order
    private int setOrder(Savepoint savepoint, Connection conn, PreparedStatement psSetOrder, Customer customer, Order order) {
        int orderId = -1;
        int updateRow;
        try {
            psSetOrder.setInt(1, customer.getId());
            psSetOrder.setInt(2, order.getOrder_price());
            psSetOrder.setDate(3, new java.sql.Date(new Date().getTime()));
//                     psSetOrder.setDate(3, new java.sql.Date(new Date().getTime()));
            updateRow = psSetOrder.executeUpdate();
            if (updateRow != 1) {
                conn.rollback(savepoint);
                logger.error("Insert into table Order is failed. We insert: " + updateRow + " rows for Order: " + order.toString());
            } else {
                ResultSet resultSet = psSetOrder.getGeneratedKeys();
                if (resultSet.next()) {
                    orderId = resultSet.getInt(1);
                    logger.info("Insert into table Order complete. Order " + order.toString() + " is added. orderId is: " + orderId);
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

    // 2 Set MIO
    private boolean moveMedicineFromCartToOrder(Savepoint savepoint, Connection conn, PreparedStatement psMedicineInOrder,
                                                int orderId, Set<MedicineInOrder> medicineForBuy) {
        boolean resultUpdateMedicineInOrder = false;
        int updateRow = 0;
        int countMedicine = 0;
        try {
            for (MedicineInOrder medicineItem : medicineForBuy) {
                countMedicine++;
                psMedicineInOrder.setString(1, medicineItem.getMedicine());
                psMedicineInOrder.setString(2, medicineItem.getDosage());
                psMedicineInOrder.setBoolean(3, medicineItem.isRecipe_required());
                psMedicineInOrder.setInt(4, medicineItem.getIndivisible_amount());
                psMedicineInOrder.setInt(5, medicineItem.getQuantity());
                psMedicineInOrder.setInt(6, medicineItem.getPriceForOne());
                psMedicineInOrder.setInt(7, orderId);
                updateRow += psMedicineInOrder.executeUpdate();
            }
            if (updateRow != countMedicine) {
                conn.rollback(savepoint);
                logger.error("Error move MedicineInOrder from Cart to Order . We insert: " + updateRow + " but was expected " + countMedicine);
            } else {
                resultUpdateMedicineInOrder = true;
                logger.info("Move MedicineInOrder from Cart to Order complete. Moved MedicineInOrder: " + medicineForBuy.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultUpdateMedicineInOrder;
    }

    // 3 Set Recipe
    private boolean snapRecipe(Savepoint savepoint, Connection conn, PreparedStatement psRecipe, int orderId,
                               Set<MedicineInOrder> medicineForBuy, Set<Recipe> recipes) {
        boolean resultUpdateRecipe = false;
        int updateRow = 0;
        int countMedicineReqRecipe = 0;
        int countUpdatedRecipe = 0;
        try {
            Set<Recipe> recipesForUpdate = new HashSet<>();
            for (MedicineInOrder medicineItem : medicineForBuy) {
                if (medicineItem.isRecipe_required()) {
                    countMedicineReqRecipe++;
                    for (Recipe recipeItem : recipes) {
                        if ((recipeItem.getMedicine().equals(medicineItem.getMedicine()) &&
                                recipeItem.getDosage().equals(medicineItem.getDosage()))) {
                            recipeItem.setId_medicine_in_order(orderId);
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
                    logger.info("Set orderId in Recipe complete. Was updated " + updateRow + " rows for orderId=" + orderId);
                } else {
                    logger.error("Error count updated Recipes in DB = " + countUpdatedRecipe + ", but was expected " + countMedicineReqRecipe);
                    conn.rollback(savepoint);
                }
            } else {
                conn.rollback(savepoint);
                logger.error("Error set orderId in Recipe. We get to update " + countUpdatedRecipe + " recipes, but was expected " + countMedicineReqRecipe);
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

    private boolean medicineEquals(Medicine realMedicine, MedicineInOrder medicineInOrder) {
        boolean name = realMedicine.getName().equals(medicineInOrder.getMedicine());
        boolean dosage = realMedicine.getDosage().equals(medicineInOrder.getDosage());
        boolean price = realMedicine.getPrice() == medicineInOrder.getPriceForOne();
        return name && dosage && price;
    }

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
                logger.error("Update into table Medicine is failed. It was updated " + countUpdateRowsMedicine
                        + ". Was updated " + realMedicineSet.size() + " rows.");
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
            logger.error("SQL Exception in update amount medicine: " + e);
        }
        return resultUpdate;
    }

    // 5
    public boolean updateAccountBalance(Savepoint savepoint, Connection conn, PreparedStatement psAccount, int customerId, int balance) {
        boolean result = false;
        try {
            psAccount.setInt(1, balance);
            psAccount.setInt(2, customerId);
            int updateRow = psAccount.executeUpdate();
            if (updateRow != 1) {
                conn.rollback(savepoint);
                logger.error("Update in table Account is failed. We update: " + updateRow + " rows for accountId: " + customerId);
            } else {
                result = true;
                logger.info("Update in table Account complete. For account id = " + customerId + " was set balance: " + balance);
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


    public void updateMedicineInCart(int idMedicine, int quantity) {
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(OrderSQL.UPDATE_MEDICINES_QUANTITY_IN_CART_BY_ID.getQuery())
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
            logger.error("SQL Exception when update medicine in cart. Medicine id = " + idMedicine + ". " + e);
        }
    }

    public void deleteMedicineFromOrder(int medicineId) {
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(OrderSQL.DELETE_MEDICINE_BY_ID.getQuery())
        ) {
            statement.setInt(1, medicineId);
            int countUpdateRowsMedicine = statement.executeUpdate();
            if (countUpdateRowsMedicine != 1) {
                logger.error("Delete from table MedicineInOrder is failed. There Was deleted " + countUpdateRowsMedicine + " rows.");
            } else {
                logger.info("Delete from table MedicineInOrder complete. We delete next medicine id = " + medicineId);
            }
        } catch (SQLException e) {
            logger.error("SQL Exception in delete medicine. Medicine Id = " + medicineId + ". " + e);
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
                PreparedStatement statement = conn.prepareStatement(OrderSQL.FIND_MEDICINE_IN_CART_BY_NAME_DOSAGE_AMOUNT.getQuery())
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
            logger.error("SQL Exception in method findMedicineInOrderByMedicine with medicine: id = " + medicine.getId()
                    + ", name = " + medicine.getName() + ", dosage = " + medicine.getDosage() + ". " + e);
        }
        return resultMedicine;
    }

    public Map<Order, Set<MedicineInOrder>> getAllOrdersWithDetails(int customerId) {
        Comparator<Order> orderComparator = new Order.OrderIdComparator();
        Set<Order> orders = new TreeSet<>(orderComparator);
        Comparator<MedicineInOrder> medicineInOrderComparator = new MedicineInOrder.NameComparator()
                .thenComparing(new MedicineInOrder.NameComparator()).thenComparing(new MedicineInOrder.DosageComparator());
        Set<MedicineInOrder> orderDetails = new TreeSet<>(medicineInOrderComparator);
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
        Comparator<Order> orderComparator = new Order.OrderIdComparator();
        Comparator<MedicineInOrder> medicineInOrderComparator = new MedicineInOrder.NameComparator()
                .thenComparing(new MedicineInOrder.DosageComparator().thenComparing(new MedicineInOrder.PriceComparator()));
        Set<Order> orders = new TreeSet<>(orderComparator);
        Set<MedicineInOrder> orderDetails = new TreeSet<>(medicineInOrderComparator);
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

    public Set<MedicineInOrder> findMedicineInCartWithActualPrice(int cartId, boolean needActualPrice) {
        Comparator<MedicineInOrder> comparator = new MedicineInOrder.NameComparator().thenComparing(
                new MedicineInOrder.DosageComparator().thenComparing(new MedicineInOrder.PriceComparator()));
        Set<MedicineInOrder> cartDetails = new TreeSet<>(comparator);
        String sql;
        if (needActualPrice) {
            sql = OrderSQL.FIND_ACTUAL_PRICE_FOR_MEDICINES_IN_CART_BY_CART_ID.getQuery();
        } else {
            sql = OrderSQL.FIND_MEDICINE_IN_CART_BY_ORDER_ID.getQuery();
        }
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            statement.setInt(1, cartId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                cartDetails.add(createMedicineInOrderFromDB(rs, needActualPrice));
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
        Comparator<Order> orderComparator = new Order.OrderIdComparator();
        Comparator<MedicineInOrder> medicineInOrderComparator = new MedicineInOrder.NameComparator()
                .thenComparing(new MedicineInOrder.DosageComparator().thenComparing(new MedicineInOrder.PriceComparator()));
        Map<Order, Set<MedicineInOrder>> ordersWithDetails = new TreeMap<>(orderComparator);
        for (Order order : orders) {
            Set<MedicineInOrder> thisOrderDetails = new TreeSet<>(medicineInOrderComparator);
            for (MedicineInOrder orderDetail : orderDetails) {
                if (order.getId() == orderDetail.getFk_order()) {
                    thisOrderDetails.add(orderDetail);
                }
            }
            ordersWithDetails.put(order, thisOrderDetails);
        }
        return ordersWithDetails;
    }
}
