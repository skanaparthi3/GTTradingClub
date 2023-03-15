package org.example.bybithandler;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.SneakyThrows;
import org.example.engine.Hud2;
import org.example.engine.OrderDisplay;
import org.example.orderbook.Order;
import org.example.orderbook.OrderBook;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class ByBitClient extends TextWebSocketHandler {
    @Getter
    private HashMap<String, OrderBook> orderbooks;
    private ObjectMapper jsonMapper = new ObjectMapper();
    public WebSocketSession getClientSession() {
        return clientSession;
    }
    private WebSocketSession clientSession;
    private long lastUpdate;

    public ByBitClient(HashMap<String, OrderBook> orderbooks) throws ExecutionException, InterruptedException {
        this.orderbooks = orderbooks;
        var webSocketClient = new StandardWebSocketClient();
        this.clientSession = webSocketClient.doHandshake(this, new WebSocketHttpHeaders(), URI.create("wss://stream.bybit.com/v5/public/linear")).get();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        System.out.println(message.getPayload());
        try {
            JsonNode response = jsonMapper.readTree(message.getPayload());
            String security = jsonMapper.convertValue(response.get("topic"), String.class);
            String[] words = security.split(Pattern.quote("."));
            security = words[words.length - 1];
            System.out.println(security);
            JsonNode orderbookData = response.get("data");
            List<List> apiBids = jsonMapper.convertValue(orderbookData.get("b"), ArrayList.class);
            List<List> apiAsks = jsonMapper.convertValue(orderbookData.get("a"), ArrayList.class);
            TreeMap<Double, Double> bids = orderbooks.get(security).bids;
            TreeMap<Double, Double> asks = orderbooks.get(security).asks;
            System.out.println(apiBids);
            if (jsonMapper.convertValue(response.get("type"), String.class).equals("snapshot")) {
                bids.clear();
                asks.clear();
            }
            for (List order : apiBids) {
                if (Double.parseDouble((String) order.get(1)) == 0) bids.remove(Double.parseDouble((String) order.get(0)));
                else bids.put(Double.parseDouble((String) order.get(0)), Double.parseDouble((String) order.get(1)));
            }
            for (List order : apiAsks) {
                if (Double.parseDouble((String) order.get(1)) == 0) asks.remove(Double.parseDouble((String) order.get(0)));
                else asks.put(Double.parseDouble((String) order.get(0)), Double.parseDouble((String) order.get(1)));
            }
            if (System.currentTimeMillis() - lastUpdate > 100) {
                Hud2.updateOrderbookData(asks, bids);
                lastUpdate = System.currentTimeMillis();
            }
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Failed to read json");
            return;
        }
    }
}


