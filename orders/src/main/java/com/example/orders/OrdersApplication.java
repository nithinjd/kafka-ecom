package com.example.orders;

import com.example.common.Order;
import com.example.orders.service.OrderManagementService;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.Duration;
import java.util.concurrent.Executor;

import static org.apache.kafka.streams.kstream.Materialized.as;

@EnableKafkaStreams
@SpringBootApplication
public class OrdersApplication {

	private static final Logger LOG = LoggerFactory.getLogger(OrdersApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(OrdersApplication.class, args);
	}
	@Autowired
	public OrderManagementService orderManagementService;
	@Bean
	public KStream<Long, Order> stream(StreamsBuilder builder, OrderManagementService orderManagementService) {
		JsonSerde<Order> orderSerde = new JsonSerde<>(Order.class);

		// Stream from "payment-orders" topic
		KStream<Long, Order> paymentStream = builder.stream(
				"payment-orders", Consumed.with(Serdes.Long(), orderSerde));

		// Join "payment-orders" with "stock-orders"
		paymentStream.join(
						builder.stream("stock-orders", Consumed.with(Serdes.Long(), orderSerde)),
						orderManagementService::confirm, // Joiner function
						JoinWindows.of(Duration.ofSeconds(10)), // Window duration
						StreamJoined.with(Serdes.Long(), orderSerde, orderSerde)) // Serdes
				.peek((key, order) -> LOG.info("Output: Key={}, Order={}")) // Log result
				.to("orders", Produced.with(Serdes.Long(), orderSerde)); // Send to "orders" topic

		return paymentStream; // Return the stream for further processing
	}

	@Bean
	public KTable<Long,Order> table(StreamsBuilder builder){
		KeyValueBytesStoreSupplier store = Stores.persistentKeyValueStore("orders");
		JsonSerde<Order> serde = new JsonSerde<>(Order.class);
		KStream<Long, Order> stream = builder.stream("orders", Consumed.with(Serdes.Long(),serde));
		return stream.toTable(Materialized.<Long,Order>as(store).withKeySerde(Serdes.Long()).withValueSerde(serde));
	}

	@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);
		executor.setMaxPoolSize(5);
		executor.setThreadNamePrefix("kafkaSender-");
		executor.initialize();
		return executor;
	}
}
