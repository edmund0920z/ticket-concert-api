package com.ticket_system.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ticket_system.model.Product;
import com.ticket_system.repository.ProductRepository;

@Service
public class ProductService {
	
	@Autowired
    private ProductRepository productRepository;
	
	//----查詢全部----
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    //----根據 ID 查詢單一商品----
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到 ID 為 " + id + " 的商品"));
    }
    
    //----新增商品----
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
	
    //----更新商品----
    public Product updateProduct(Long id, Product updatedProduct) {
        Product existingProduct = getProductById(id);
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setStock(updatedProduct.getStock());
        return productRepository.save(existingProduct);
    }
    
    //----刪除商品----
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
}
