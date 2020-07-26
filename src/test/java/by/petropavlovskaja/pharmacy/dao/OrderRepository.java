package by.petropavlovskaja.pharmacy.dao;

import by.petropavlovskaja.pharmacy.Repository;
import by.petropavlovskaja.pharmacy.dao.sql.OrderSQL;
import by.petropavlovskaja.pharmacy.model.Order;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository extends Repository<Order> {
    protected static final List<Order> ORDERS_AND_CARTS = new ArrayList<>();
    protected static final List<Order> ORDERS = new ArrayList<>();
    protected static final List<Order> CARTS = new ArrayList<>();
    private static final String CREATE_TABLE = "CREATE TABLE \"order\" (id int NOT NULL GENERATED ALWAYS AS IDENTITY,  " +
            "fk_customer int NOT NULL, payment_state boolean NOT NULL DEFAULT false, order_price int NOT NULL DEFAULT 0, " +
            "order_date timestamp NOT NULL, cart boolean NOT NULL DEFAULT false);";
    static final String INSERT_ORDER_SQL = OrderSQL.INSERT_ORDER.getQuery();
    static final String INSERT_CART_SQL = OrderSQL.INSERT_CART.getQuery();

    public OrderRepository() throws SQLException {
    }

    private java.util.Date getDate(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.parse(date);
    }

    public void initializeMembers() throws ParseException {
        CARTS.add(new Order(1, 78, 8756, getDate("2019-08-18"), true));
        CARTS.add(new Order(8, 44, 853, getDate("2019-08-20"), true));
        ORDERS.add(new Order(104, 44, 8324, getDate("2019-11-10"), false));
        ORDERS.add(new Order(264, 8, 56, getDate("2020-02-06"), false));
        ORDERS.add(new Order(264, 8, 56, getDate("2020-02-06"), false));
        ORDERS_AND_CARTS.addAll(CARTS);
        ORDERS_AND_CARTS.addAll(ORDERS);
    }

    @Override
    public void createTable() throws SQLException {
        Statement createStatement = connection.createStatement();
        createStatement.execute(CREATE_TABLE);
    }

    @Override
    public void insertMembers() throws SQLException {
        final PreparedStatement ps = connection.prepareStatement(INSERT_ORDER_SQL);
        ORDERS.forEach(member -> insertOrder(member, ps));
        ps.executeBatch();
        final PreparedStatement ps1 = connection.prepareStatement(INSERT_CART_SQL);
        CARTS.forEach(member -> insertCart(member, ps));
        ps1.executeBatch();
    }

    private static void insertOrder(Order order, PreparedStatement insertMembers) {
        try {
            insertMembers.setInt(1, order.getFkCustomer());
            insertMembers.setInt(2, order.getOrderPrice());
            insertMembers.setDate(3, new java.sql.Date(order.getOrderDate().getTime()));
            insertMembers.addBatch();
        } catch (SQLException e) {
            throw new UnsupportedOperationException(e.getMessage());
        }
    }

    private static void insertCart(Order order, PreparedStatement insertMembers) {
        try {
            insertMembers.setInt(1, order.getFkCustomer());
            insertMembers.addBatch();
        } catch (SQLException e) {
            throw new UnsupportedOperationException(e.getMessage());
        }
    }

    public void closeConnection() throws SQLException {
        connection.createStatement().execute("DROP TABLE recipe");
    }
}
