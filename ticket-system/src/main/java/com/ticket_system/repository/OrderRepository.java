package com.ticket_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ticket_system.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>{
	//查詢特定會員的所有定單
	List<Order>findByUserId(Long userId);
}
