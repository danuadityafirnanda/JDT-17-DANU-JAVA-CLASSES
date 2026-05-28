package com.indivaragroup.shop.dto;

import java.math.BigDecimal;

public class ItemDTO {
    public String name;
    public BigDecimal price;
    public BigDecimal quantity;

    public ItemDTO(String name, BigDecimal quantity, BigDecimal price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }
}
