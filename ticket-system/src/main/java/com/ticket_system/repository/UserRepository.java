package com.ticket_system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ticket_system.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
	// 根據帳號查詢使用者（登入時使用）
    Optional<User> findByUsername(String username);

    // 檢查帳號是否已存在（註冊時防重複）
    boolean existsByUsername(String username);
}
