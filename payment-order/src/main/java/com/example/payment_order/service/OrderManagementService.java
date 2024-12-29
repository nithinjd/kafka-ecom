package com.example.payment_order.service;


import com.example.payment_order.entity.Customer;
import com.example.common.Order;
import com.example.payment_order.repository.CustomerRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class OrderManagementService {

    private static  final String SOURCE = "payment";

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private KafkaTemplate<Long, Order> template;

    private static final Logger log = LoggerFactory.getLogger(OrderManagementService.class);

    public void addCustomer(Customer customer){
        customerRepository.save(customer);
    }
    public void reserve(Order order){
       Customer customer = customerRepository.findById(order.getCustomerId()).orElseThrow();
       if(order.getPrice()<= customer.getAmountAvailable()){
           customer.setAmountReserved(customer.getAmountReserved()+ order.getPrice());
           customer.setAmountAvailable(customer.getAmountAvailable() - order.getPrice());
           order.setStatus("ACCEPT");
       }
       else {
           order.setStatus("REJECT");
       }
        order.setSource(SOURCE);
        customerRepository.save(customer);
        template.send("payment-orders", order.getId(), order);
    }

    public void confirm(Order order){
        Customer customer = customerRepository.findById(order.getCustomerId()).orElseThrow();
        if(order.getStatus().equals("CONFIRMED")){
            customer.setAmountReserved(customer.getAmountReserved() - order.getPrice());
            customerRepository.save(customer);
        }else{
            customer.setAmountReserved(customer.getAmountReserved() - order.getPrice());
            customer.setAmountAvailable(customer.getAmountAvailable()+ order.getPrice());
            customerRepository.save(customer);
        }
    }

    public Customer getCustomerById(Long id){
        Customer customer = customerRepository.findById(id).orElseThrow();
        return customer;
    }

}
