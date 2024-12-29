package com.example.payment_order;


import com.example.common.Order;
import com.example.payment_order.service.OrderManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

@EnableKafka
@SpringBootApplication
public class PaymentOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentOrderApplication.class, args);
	}

	@Autowired
	public OrderManagementService orderManagementService;
	@KafkaListener(topics = "orders",id = "orders", groupId = "payment")
	public void onEvent(Order o){
		if(o.getStatus().equals("NEW")){
			orderManagementService.reserve(o);
		}
		else{
			orderManagementService.confirm(o);
		}
	}
}
