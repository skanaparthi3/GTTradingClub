package org.example.orderbook;

public class Order {
    public double price;
    public double shares;
    Order(double price, double shares) {
        this.price = price;
        this.shares = shares;
    }
}
