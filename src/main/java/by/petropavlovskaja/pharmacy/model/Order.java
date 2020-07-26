package by.petropavlovskaja.pharmacy.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

/**
 * Class for order entity. Has next properties:
 * <b>serialVersionUID</b>, <b>id</b>, <b>fkCustomer</b>, <b>orderPrice</b>, <b>orderDate</b>,
 * <b>cart</b>, <b>rub</b> and <b>coin</b>
 */
public class Order implements Serializable {
    /**
     * Property - serial version UID
     */
    private static final long serialVersionUID = -5957381211186194380L;
    /**
     * Property - order ID
     */
    private int id;
    /**
     * Property - customer foreign key
     */
    private int fkCustomer;
    /**
     * Property - order price
     */
    private int orderPrice;
    /**
     * Property - order date
     */
    private Date orderDate;
    /**
     * Property - is order a cart
     */
    private boolean cart;
    // local variable below don't save in database, they are for JSP
    /**
     * Property - price (part in the pub)
     */
    private int rub; // currency unit
    /**
     * Property - price (part in the coin)
     */
    private int coin; // currency unit

    /**
     * Create entity of class {@link Order#Order(int, int, int, Date, boolean)}
     *
     * @param id - recipe ID
     */
    public Order(int id) {
        this.id = id;
    }

    /**
     * Create entity of class {@link Order#Order(int)}
     *
     * @param id         - recipe ID
     * @param fkCustomer - customer foreign key
     * @param orderDate  - order date
     * @param orderPrice - order price
     * @param cart       - is order a cart
     */
    public Order(int id, int fkCustomer, int orderPrice, Date orderDate, boolean cart) {
        this.id = id;
        this.fkCustomer = fkCustomer;
        this.orderPrice = orderPrice;
        this.orderDate = orderDate;
        this.cart = cart;
    }

    /**
     * The method of getting the order ID field value
     *
     * @return - an order ID value
     */
    public int getId() {
        return id;
    }

    /**
     * The method of getting the foreign key field value
     *
     * @return - a foreign key value
     */
    public int getFkCustomer() {
        return fkCustomer;
    }

    /**
     * The method of getting the order price field value
     *
     * @return - an order price value
     */
    public int getOrderPrice() {
        return orderPrice;
    }

    /**
     * The method of getting the order date field value
     *
     * @return - an order date value
     */
    public Date getOrderDate() {
        return orderDate;
    }

    /**
     * The method of getting is the order field a cart
     *
     * @return - is the order field a cart value
     */
    public boolean isCart() {
        return cart;
    }

    /**
     * The method of getting the customer FIO field value
     *
     * @return - a customer FIO value
     */
    public int getRub() {
        return rub;
    }

    /**
     * The method for setting the rub field
     *
     * @param rub - a rub part of order price
     */
    public void setRub(int rub) {
        this.rub = rub;
    }

    /**
     * The method of getting the coin field value
     *
     * @return - a coin value
     */
    public int getCoin() {
        return coin;
    }

    /**
     * The method for setting the coin field
     *
     * @param coin - a coin part of order price
     */
    public void setCoin(int coin) {
        this.coin = coin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id == order.id;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", fkCustomer=" + fkCustomer +
                ", orderPrice=" + orderPrice +
                ", orderDate=" + orderDate +
                ", status=" + cart +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderPrice);
    }

    /**
     * The nested class for compare order entity {@link Order.OrderIdComparator},
     * {@link Order.OrderPriceComparator}
     */
    public static class OrderDateComparator implements Comparator<Order> {

        /**
         * The method compare order by order date
         *
         * @param a - one order
         * @param b - another order
         * @return - difference between two orders
         */
        public int compare(Order a, Order b) {
            return a.getOrderDate().compareTo(b.getOrderDate());
        }
    }

    /**
     * The nested class for compare order entity {@link Order.OrderDateComparator},
     * {@link Order.OrderPriceComparator}
     */
    public static class OrderIdComparator implements Comparator<Order> {

        /**
         * The method compare order by order ID
         *
         * @param a - one order
         * @param b - another order
         * @return - difference between two orders
         */
        public int compare(Order a, Order b) {
            return a.getId() - b.getId();
        }
    }

    /**
     * The nested class for compare order entity {@link Order.OrderIdComparator},
     * {@link Order.OrderDateComparator}
     */
    public static class OrderPriceComparator implements Comparator<Order> {

        /**
         * The method compare order by order price
         *
         * @param a - one order
         * @param b - another order
         * @return - difference between two orders
         */
        public int compare(Order a, Order b) {
            return a.getOrderPrice() - b.getOrderPrice();
        }
    }
}
