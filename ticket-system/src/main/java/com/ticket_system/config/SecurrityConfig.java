package com.ticket_system.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurrityConfig {
	
	@Autowired
	private JwtAuthenticationFilter jwtAuthFilter;
	
	@Bean
	public SecurityFilterChain sercurityFilterChain(HttpSecurity http) throws Exception{
		http
			//使用Spring Security 6 標準語法關閉 CSRF
			.csrf(csrf->csrf.disable())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth->auth
					//允許公開存取的API(註冊、登入)
					.requestMatchers("/api/v1/users/login", "/error").permitAll()//登入/註冊公開
					//其餘所有 API 都必須攜帶有效的 JWT Token 才能訪問
					.anyRequest().authenticated()
			)
			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
	
}
