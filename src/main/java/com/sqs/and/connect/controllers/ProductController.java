package com.sqs.and.connect.controllers;

import java.util.List;

import com.sqs.and.connect.models.Product;
import com.sqs.and.connect.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rds/products")
public class ProductController {

    @Autowired ProductRepository productRepository;


    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        try {
            return productRepository.saveAndFlush(product);
        } catch (Exception e) {
            System.out.println("Something went wrong with writing into DB - " + e);
            return null;
        }
    }
}
