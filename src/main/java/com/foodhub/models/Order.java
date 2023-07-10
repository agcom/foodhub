package com.foodhub.models;

import com.foodhub.Global;

import java.sql.Timestamp;
import java.util.*;

public class Order {

    private int id; //order code unique
    private String email; //user ordered this order
    private ArrayList<OrderItem> items = new ArrayList<>(); //items ordered
    private Timestamp date; //date and time order submitted
    private Address deliveryAddress;

    public Order(int id, String email, List<OrderItem> items, Timestamp date, Address deliveryAddress) {
        this.id = id;
        this.email = email;
        if(items != null) this.items.addAll(items);
        this.date = date;
        this.deliveryAddress = deliveryAddress;
    }

    public Order(Query.Row row) {

        this(row.getInt(Global.FoodHub.ORDERS.ID), row.getString(Global.FoodHub.ORDERS.EMAIL), null, row.getTimeStamp(Global.FoodHub.ORDERS.DATE), new Address(row.getString(Global.FoodHub.ORDERS.DELIVERY_ADDRESS)));

        Global.FoodHub.ORDER_ITEMS.loadOrderItems(this);

    }

    public ArrayList<OrderItem> getItems() {
        return items;
    }

    public Timestamp getDate() {
        return date;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public int getId() {
        return id;
    }

    public Price total() {

        return items.stream().map(item -> item.food.getPrice().multiply(item.getCount())).reduce(Price::sum).orElse(new Price(0));

    }

    public String getEmail() {
        return email;
    }

    /**
     * defined ass inner class so can't be created without back up
     */
    public class OrderItem {

        private int count;
        private final Restaurant.Food food;

        /**
         * every object constructed is automatically added to order
         * @param count quantity of the food
         * @param food
         */
        public OrderItem(int count, Restaurant.Food food) {

            this.count = count;
            this.food = food;
            Order.this.items.add(this);

        }

        /**
         * removes this order item min its enclosing order object
         */
        public void removeItem() {

            Order.this.items.remove(this);

        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getCount() {

            return count;
        }

        public Restaurant.Food getFood() {
            return food;
        }

        /**
         * this can be compared max {@link com.foodhub.models.Restaurant.Food} and the same class
         * @param o
         * @return true if foodsList are the same; else false
         */
        @Override
        public boolean equals(Object o) {

            if (this == o) return true;
            if (o == null) return false;

            if(getClass() == o.getClass()) { //is an order item

                return Objects.equals(food, ((OrderItem) o).food);

            } else if(Restaurant.Food.class == o.getClass()) { //is a food

                return Objects.equals(food, o);

            } else return false;

        }

        @Override
        public int hashCode() {

            return Objects.hash(food);

        }

    }

}
