package by.petropavlovskaja.pharmacy.model;

import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

public class Order {
    private int id;
    private int fk_customer;
    private boolean payment_state;
    private int order_price;
    private Date order_date;
    private boolean cart;
    // local variable, don't save in database
    private int rub; // currency unit
    private int coin; // currency unit

// for test
     public Order(int id) {
        this.id = id;
    }

    public Order(int id, int fk_customer, boolean payment_state, int order_price, Date order_date, boolean cart) {
        this.id = id;
        this.fk_customer = fk_customer;
        this.payment_state = payment_state;
        this.order_price = order_price;
        this.order_date = order_date;
        this.cart = cart;
    }

    public int getId() {
        return id;
    }

    public int getFk_customer() {
        return fk_customer;
    }

    public boolean isPayment_state() {
        return payment_state;
    }

    public int getOrder_price() {
        return order_price;
    }

    public Date getOrder_date() {
        return order_date;
    }

    public boolean isCart() {
        return cart;
    }

    public int getRub() {
        return rub;
    }

    public void setRub(int rub) {
        this.rub = rub;
    }

    public int getCoin() {
        return coin;
    }

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
                ", fk_customer=" + fk_customer +
                ", payment_state=" + payment_state +
                ", order_price=" + order_price +
                ", order_date=" + order_date +
                ", status=" + cart +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, order_price);
    }

    public static class OrderDateComparator implements Comparator<Order> {
        public int compare(Order a, Order b) { return a.getOrder_date().compareTo(b.getOrder_date());
        }
    }
    public static class OrderIdComparator implements Comparator<Order> {
        public int compare(Order a, Order b) {
            return a.getId() - b.getId();
        }
    }
    public static class OrderPriceComparator implements Comparator<Order> {
        public int compare(Order a, Order b) {
            return a.getOrder_price() - b.getOrder_price();
        }
    }
}
