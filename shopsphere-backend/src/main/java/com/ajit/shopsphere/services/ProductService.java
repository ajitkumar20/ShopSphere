package com.ajit.shopsphere.services;

import java.util.List;
import java.util.UUID;

import com.ajit.shopsphere.dtos.ProductDto;
import com.ajit.shopsphere.entities.Product;

public interface ProductService {

    public Product addProduct(ProductDto productDto);

    public List<ProductDto> getAllProducts(UUID categoryId, UUID typeId);

    public ProductDto getProductBySlug(String slug);

    public ProductDto getProductById(UUID id);

    public Product updateProduct(ProductDto productDto);
}
