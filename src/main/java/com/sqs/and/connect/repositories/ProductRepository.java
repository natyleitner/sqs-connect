package com.sqs.and.connect.repositories;

import com.sqs.and.connect.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {}
