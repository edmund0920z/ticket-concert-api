package com.ticket_system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class OrderRequestDto {
	
	@NotNull(message="productId 為必填欄位")
	private Long productId;
	
	
	@NotNull(message="quantity 為必填欄位")
	@Min(value=1, message="購買數量至少需為 1")
	private Integer quantity;

	
	//未來要增加欄位，直接在這裡新增即可!
	
	
	//----getter and setter
	public Long getProductId() {
		return productId;
	}


	public void setProductId(Long productId) {
		this.productId = productId;
	}


	public Integer getQuantity() {
		return quantity;
	}


	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}	
	
}
