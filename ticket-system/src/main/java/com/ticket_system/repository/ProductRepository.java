package com.ticket_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ticket_system.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
	// Spring Data JPA 會自動實現基本的 CRUD (save, findById, findAll, delete)
	
	
	//原子扣庫存 SQL: 只有當現有庫存 >= 扣減數量才會更新，回傳成功更新的筆數 (1或0)
	@Transactional
	@Modifying
	@Query(value="UPDATE products SET stock = stock - :quantity, version = version + 1"+
			"WHERE id = :productId AND version = :version AND stock >= :quantity",
			nativeQuery =true)
	int decreaseStockWithOptimisticLock(@Param("productId") Long productId, 
										@Param("quantity") Integer quantity,
										@Param("version") Integer version);
}
