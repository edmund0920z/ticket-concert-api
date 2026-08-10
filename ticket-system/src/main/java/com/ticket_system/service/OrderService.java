package com.ticket_system.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ticket_system.model.Order;
import com.ticket_system.model.Product;
import com.ticket_system.model.User;
import com.ticket_system.repository.OrderRepository;
import com.ticket_system.repository.ProductRepository;
import com.ticket_system.repository.UserRepository;

@Service
public class OrderService {
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	//----含庫存扣減與交易控制----
	
	//@Transational，標柱為事務:若扣庫存或訂單任一步驟失敗，資料庫會自動關閉並復原(Roll back)
	@Transactional(rollbackFor = Exception.class) //指定所有 Exception 都觸發回滾(Roll back)
	public Order createOrder(String username, Long productId, Integer quantity) {
		
		try {
		//1. 取得會員資訊
		User user=userRepository.findByUsername(username)
				.orElseThrow(()->new RuntimeException("使用者不存在!"));
		
		//2. 檢查商品是否存在
		Product product=productRepository.findById(productId)
				.orElseThrow(()->new RuntimeException("商品不存在!"));
		
		//3. 快速預先檢查 (提供剩餘庫存提示)
		if(product.getStock()<quantity) {
			throw new RuntimeException("票券庫存不足!剩餘庫存: "+product.getStock());
		}
		
		//4. 真正進行資料庫層級的原子扣庫存(安全防線，避免超賣)
		int updateRows=productRepository.decreaseStockWithOptimisticLock(productId, quantity, product.getVersion());
		
		//5. 若回傳 0 代表在高併發情況下，票被其他人拾先扣光了
		if(updateRows == 0) {
			throw new RuntimeException("票券已售完!");
			
		}
		
		//6. 建立訂單
		Order order=new Order();
		order.setUserId(user.getId());
		order.setProductId(product.getId());
		order.setQuantity(quantity);
		order.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
		order.setOrderDate(LocalDateTime.now());
		
		return orderRepository.save(order);
		
		}catch(ObjectOptimisticLockingFailureException e) {
			//捕捉 JPA 樂觀鎖版本號衝突異常 (若後續有加上 @Version 欄位)
			throw new RuntimeException("系統繁忙，拾票人數眾多，請再試一次!");
			
		}catch (Exception e){
			//其他所有異常直接重新拋出，確保 @Transactional 能感應到並執行 Rollback
			throw e;
			
		}
		
	}
	
	//----查詢個人訂單----
	
	//查詢當前登入使用者的所有訂單
	public List<Order>getOrdersByUsername(String username){
		User user=userRepository.findByUsername(username)
				.orElseThrow(()->new RuntimeException("使用者不存在"));
		return orderRepository.findByUserId(user.getId());
	}
	
}
