package com.example.StockApp.model;


public class ProductModel {
    private Long id;
    private String name;
    private int availableItems;
    private int reservedItems;

    public ProductModel(String name, int availableItems, int reservedItems) {
        this.name = name;
        this.availableItems = availableItems;
        this.reservedItems = reservedItems;
    }

    public ProductModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAvailableItems() {
        return availableItems;
    }

    public void setAvailableItems(int availableItems) {
        this.availableItems = availableItems;
    }

    public int getReservedItems() {
        return reservedItems;
    }

    public void setReservedItems(int reservedItems) {
        this.reservedItems = reservedItems;
    }
}
