package com.ticket_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ticket_system.dto.OrderRequestDto;
import com.ticket_system.model.Order;
import com.ticket_system.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(path="/api/v1/orders")
public class OrderController {
	
	@Autowired
	private OrderService orderService;
	
	//----建立 API ----
	@PostMapping
	public ResponseEntity<?>createOrder(
			Authentication authentication, //Spring Security 會自動注入目前帶 Token 的使用者資訊
			//DTO + Validation 驗證
			@Valid @RequestBody OrderRequestDto request){ //加上 @Valid，Spring 會自動校驗欄位
		
		try {
			String username = authentication.getName();
			
			//直接使用強型別 Getter，完全不用手動轉型與 null 檢查!
			Order order=orderService.createOrder(
					username,
					request.getProductId(),
					request.getQuantity()
					);
			//成功扣庫存: 回傳 200 ok 與訂單資料
			return ResponseEntity.ok(order);
		}catch (Exception e) {
			//庫存不足或樂觀鎖併發衝突: 回傳 400 Bad Request 與錯誤訊息
			return ResponseEntity.badRequest().body(e.getMessage());
		}
			
	}
	
	//----查詢個人訂單 API----
	@GetMapping("/me")
	public ResponseEntity<?>getMyOrders(Authentication authentication){
		String username=authentication.getName(); //從 JWT 解析出來的帳號
		return ResponseEntity.ok(orderService.getOrdersByUsername(username));
	}
	
}
