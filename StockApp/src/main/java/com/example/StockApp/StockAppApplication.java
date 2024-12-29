package com.example.StockApp;

import com.example.common.Order;
import com.example.StockApp.service.OrderManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
public class StockAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockAppApplication.class, args);
	}

	@Autowired
	public OrderManagementService orderManagementService;

	@KafkaListener(topics = "orders",id = "orders", groupId = "stock")
	public void onEvent(Order o){
		if(o.getStatus().equals("NEW")){
			orderManagementService.reserve(o);
		}else{
			orderManagementService.confirm(o);
		}
	}
}
