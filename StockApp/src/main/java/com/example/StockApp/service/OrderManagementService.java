package com.example.StockApp.service;


import com.example.StockApp.Entity.Product;
import com.example.StockApp.model.ProductModel;
import com.example.StockApp.repository.ProductRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import com.example.common.Order;
import java.util.stream.Collectors;
import org.slf4j.Logger;

@Service
public class OrderManagementService {
    private static final String SOURCE = "stock";
    private static final Logger LOG = LoggerFactory.getLogger(OrderManagementService.class);

    @Autowired
    private ProductRepository repository;

    @Autowired
    private KafkaTemplate<Long, Order> template;
    public void addProduct(Product product) {
        repository.save(product);
        LOG.info("Product added: {}", product);
    }

    public void reserve(Order order){
        Product product = repository.findById(order.getProductId()).orElseThrow();
        if(order.getProductCount()<= product.getAvailableItems()){
            product.setAvailableItems(product.getAvailableItems()- order.getProductCount());
            product.setReservedItems(product.getReservedItems() + order.getProductCount());
            order.setStatus("ACCEPT");
            repository.save(product);
        }
        else{
            order.setStatus("REJECT");
        }
        order.setSource(SOURCE);
        template.send("stock-orders",order.getId(),order);

    }

    public void confirm(Order order){
        Product product = repository.findById(order.getProductId()).orElseThrow(()-> new IllegalArgumentException("Product is not avilable"+order.getProductId()));

        if(order.getStatus().equals("CONFIRMED")){
            product.setReservedItems(product.getReservedItems() - order.getProductCount());

        }else{
            product.setReservedItems(product.getReservedItems()- order.getProductCount());
            product.setAvailableItems(product.getAvailableItems()+ order.getProductCount());
        }
        repository.save(product);
    }

    // Fetch all products as models
    public List<ProductModel> getAllProducts() {
        return repository.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    // Fetch a product by ID as a model
    public Optional<ProductModel> getProductById(Long id) {
        return repository.findById(id).map(this::toModel);
    }

    // Delete a product by ID
    public void deleteProductById(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            LOG.info("Deleted product with ID: {}");
        } else {
            LOG.warn("Product with ID {} not found");
        }
    }

    // Convert Product entity to ProductModel
    private ProductModel toModel(Product product) {
        ProductModel model = new ProductModel();
        model.setId(product.getId());
        model.setName(product.getName());
        model.setAvailableItems(product.getAvailableItems());
        model.setReservedItems(product.getReservedItems());
        return model;
    }

}
