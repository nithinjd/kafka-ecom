package com.example.payment_order.controller;


import com.example.payment_order.entity.Customer;
import com.example.common.Order;
import com.example.payment_order.service.OrderManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    public OrderManagementService orderManagementService;
    @PostMapping("/customers")
    public ResponseEntity<String> addCustomer(@RequestBody Customer customer) {
        orderManagementService.addCustomer(customer);
        return ResponseEntity.ok("Customer added successfully!");
    }

    // Reserve funds for an order
    @PostMapping("/reserve")
    public ResponseEntity<String> reserveOrder(@RequestBody Order order) {
        orderManagementService.reserve(order);
        return ResponseEntity.ok("Order reservation processed!");
    }

    // Confirm or rollback an order
    @PostMapping("/confirm")
    public ResponseEntity<String> confirmOrder(@RequestBody Order order) {
        orderManagementService.confirm(order);
        return ResponseEntity.ok("Order confirmation processed!");
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        try {
            Customer customer = orderManagementService.getCustomerById(id);
            return ResponseEntity.ok(customer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}
