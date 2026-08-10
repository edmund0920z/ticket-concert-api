package com.ticket_system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ticket_system.model.Product;
import com.ticket_system.repository.ProductRepository;
import com.ticket_system.service.ProductService;

@RestController
@RequestMapping(path="/api/v1/products")
public class ProductController {
	
	@Autowired
    private ProductRepository productRepository;
	
	@Autowired
    private ProductService productService;
	
	//----取得所有商品 LIST (GET http://localhost:8080/api/products)----
    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    //----查詢商品----
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    //----新增商品 (POST http://localhost:8080/api/products)----
    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }
    
    //----更新商品----
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }
    
    //----刪除商品----
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("商品刪除成功!");
    }
    
}
