package com.example.orders.controller;


import com.example.common.Order;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger LOG = LoggerFactory.getLogger(OrderController.class);

    // AtomicLong for generating unique IDs
    private AtomicLong id = new AtomicLong();

    @Autowired
    private KafkaTemplate<Long, Order> template;

    @Autowired
    private StreamsBuilderFactoryBean kafkaStreamsFactory;


    /**
     * Create a new order and send it to the "orders" topic.
     */
    @PostMapping
    public Order create(@RequestBody Order order) {
        order.setId(id.incrementAndGet()); // Set a unique ID for the order
        template.send("orders", order.getId(), order); // Send to Kafka topic
        LOG.info("Sent: {}", order); // Log the sent order
        return order; // Return the created order
    }



    /**
     * Fetch all orders from the state store.
     */
    @GetMapping
    public List<Order> all() {
        List<Order> orders = new ArrayList<>();

        // Get the state store named "orders"
        ReadOnlyKeyValueStore<Long, Order> store = kafkaStreamsFactory
                .getKafkaStreams()
                .store(StoreQueryParameters.fromNameAndType(
                        "orders",
                        QueryableStoreTypes.keyValueStore()));

        // Iterate through all entries in the store
        KeyValueIterator<Long, Order> it = store.all();
        it.forEachRemaining(kv -> orders.add(kv.value)); // Add to the list

        return orders; // Return the list of orders
    }
}