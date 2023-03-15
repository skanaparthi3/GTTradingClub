package org.example.engine;

import javafx.beans.property.SimpleDoubleProperty;

public class OrderDisplay {
    private SimpleDoubleProperty askPrice, askVolume, bidPrice, bidVolume;

    public OrderDisplay() {
        askPrice = new SimpleDoubleProperty();
        askVolume = new SimpleDoubleProperty();
        bidPrice = new SimpleDoubleProperty();
        bidVolume = new SimpleDoubleProperty();
    }
    public OrderDisplay(OrderType orderType, Double price, Double volume) {
        if (orderType == OrderType.ASK) {
            askPrice = new SimpleDoubleProperty(price);
            askVolume = new SimpleDoubleProperty(volume);
            bidPrice = new SimpleDoubleProperty();
            bidVolume = new SimpleDoubleProperty();
        } else if (orderType == OrderType.BID) {
            bidPrice = new SimpleDoubleProperty(price);
            bidVolume = new SimpleDoubleProperty(volume);
            askPrice = new SimpleDoubleProperty();
            askVolume = new SimpleDoubleProperty();
        } else {
            throw new IllegalArgumentException("Invalid order type");
        }
    }

    public Double getAskPrice() {
        return (askPrice.get() != 0) ? askPrice.get() : null;
    }

    public Double getAskVolume() {
        return (askVolume.get() != 0) ? askVolume.get() : null;
    }

    public Double getBidPrice() {
        return (bidPrice.get() != 0) ? bidPrice.get() : null;
    }

    public Double getBidVolume() {
        return (bidVolume.get() != 0) ? bidVolume.get() : null;
    }
}