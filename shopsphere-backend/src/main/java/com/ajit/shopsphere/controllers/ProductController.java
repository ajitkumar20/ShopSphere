package com.ajit.shopsphere.controllers;

import java.util.Collections;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ajit.shopsphere.dtos.ProductDto;

@RestController
@RequestMapping("/product")
public class ProductController {
    
    @GetMapping
    public List<ProductDto> getAllProducts(){
        return Collections.emptyList();
    }

    @PostMapping
    public ProductDto createProduct(@RequestBody ProductDto productDto){
        return null;
    }
}
