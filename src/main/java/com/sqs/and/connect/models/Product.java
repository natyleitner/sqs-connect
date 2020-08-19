package com.sqs.and.connect.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="Products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    public Product(String product, double price){
        this.product = product;
        this.price = price;
    }

    @Id
    @GeneratedValue
    private int id;

    private String product;

    private double price;
}
