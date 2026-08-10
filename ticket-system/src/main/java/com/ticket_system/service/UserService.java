package com.ticket_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ticket_system.model.User;
import com.ticket_system.repository.UserRepository;
import com.ticket_system.util.JwtUtils;

@Service
public class UserService {
	
	@Autowired
    private UserRepository userRepository;
	
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    //----會員註冊---
    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("帳號已被使用！");
        }

        // 將明碼密碼用 BCrypt 加密後再存入資料庫
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // 預設賦予一般使用者權限
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("ROLE_USER");
        }

        return userRepository.save(user);
    }
    
    //----登入邏輯----
    @Autowired
    private JwtUtils jwtUtils;
    
    //會員登入驗證並取得 Token
    public String login(String username, String rawPassword) {
    	//1. 檢查帳號是否存在
    	User user=userRepository.findByUsername(username).orElseThrow(()->new RuntimeException("帳號或密碼錯誤!"));
    	
    	//2. 比對加密密碼
    	if(!passwordEncoder.matches(rawPassword, user.getPassword())) {
    		throw new RuntimeException("帳號或密碼錯誤!");
    	}
    	
    	//3. 驗證通過，發放 JWT Token
    	return jwtUtils.generateToken(user.getUsername(), user.getRole());
    }
	
}
