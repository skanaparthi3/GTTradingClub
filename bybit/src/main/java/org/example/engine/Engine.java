package org.example.engine;

import lombok.SneakyThrows;
import org.example.bybithandler.ByBitClient;
import org.example.orderbook.Order;
import org.example.orderbook.OrderBook;
import org.springframework.web.socket.TextMessage;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class Engine {
    private static HashMap<String, OrderBook> orderbooks = new HashMap<String, OrderBook>();
    @SneakyThrows
    public static void main(String[] args) {
        // Create barrier and set countdown counter to 1
        CountDownLatch doneSignal = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            /**
             * Callback for Control-c
             */
            @Override
            public void run() {
                // Unlock the latch with main thread awaiting for it, will continue and do the clean up
                doneSignal.countDown();
            }
        });

        ByBitClient sampleClient;
        orderbooks.put("BTCUSDT", new OrderBook());
        orderbooks.put("ETHUSDT", new OrderBook());
        try {
            sampleClient =  new ByBitClient(orderbooks);
        } catch (Exception e) {
            System.out.println("Couldn't create ByBitClient");
            return;
        }
        try {
            sampleClient.getClientSession().sendMessage(new TextMessage("{\"req_id\": \"100001\", \"op\": \"ping\"}"));
            String orderbookSubscribe = "{\n" +
                    "  \"req_id\": \"test\",\n" +
                    "  \"op\": \"subscribe\",\n" +
                    "  \"args\": [\n" +
                    "    \"orderbook.50.BTCUSDT\",\n" +
                    "    \"orderbook.50.ETHUSDT\"\n" +
                    "  ]\n" +
                    "}";
            sampleClient.getClientSession().sendMessage(new TextMessage(orderbookSubscribe));
        } catch (Exception e) {
            System.out.println("Couldn't receive message from ByBitClient session");
        }

        // Block in wait state till unlocked by pressing Control-c
        try {
            doneSignal.await();
            sampleClient.getClientSession().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Exiting.");
    }
}
