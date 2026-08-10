package com.ticket_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ticket_system.model.User;
import com.ticket_system.service.UserService;

@RestController
@RequestMapping(path="/api/v1/users")
public class UserController {
	
	@Autowired
    private UserService userService;
	
	//----註冊 API----
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            // 為了資安，回傳給前端時把密碼遮蔽掉
            registeredUser.setPassword("******");
            return ResponseEntity.ok(registeredUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    //----登入 API---
    @PostMapping("/login")
    public ResponseEntity<?>login(@RequestBody User loginRequest){
    	try {
    		String token=userService.login(loginRequest.getUsername(), loginRequest.getPassword());
    		//回傳帶有 Token 的結果
    		return ResponseEntity.ok(java.util.Map.of(
    				"message", "登入功成",
    				"token", token
    				));
    	}catch(RuntimeException e) {
    		return ResponseEntity.badRequest().body(e.getLocalizedMessage());
    	}
    }
	
}
