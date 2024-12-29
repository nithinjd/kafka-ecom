package com.example.common;




public class Order {

    private Long id;
    private Long productId;
    private int productCount;
    private String status;
    private String source;
    private Long customerId;
    private int price;
    public Order() {
    }

    public Order(Long id, Long customerId, Long productId, int productCount, int price) {
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Order(Long productId, int productCount, String status, String source, Long customerId, int price) {
        this.productId = productId;
        this.productCount = productCount;
        this.status = status;
        this.source = source;
        this.customerId = customerId;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
