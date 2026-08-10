package com.ticket_system.config;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ticket_system.util.JwtUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	
	@Autowired
	private JwtUtils jwtUtils;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException{
		
		//1. 從Request Header 取得 Authorization
		final String authHeader=request.getHeader("Authorization");
		final String username;
		final String jwtToken;
		
		//2. 檢查 Header 是否存在且開頭為 "Bearer"
		if(authHeader == null || !authHeader.startsWith("Bearer")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		//3. 攝取 Token 本體
		jwtToken=authHeader.substring(7);
		try {
			username=jwtUtils.extractUsername(jwtToken);
			
			//4. 若解析出帳號且目前尚未驗證身份
			if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				String role = jwtUtils.extractRole(jwtToken);
				
				if(jwtUtils.validateToken(jwtToken, username)) {
					UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(
							username,
							null,
							Collections.singletonList(new SimpleGrantedAuthority(role))
					);
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					
					//5. 將驗證哥過的身份放進 SecurityContext
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}
		}catch(Exception e) {
			//Token 解析失敗或過期，交給後續處理
		}
		
		filterChain.doFilter(request, response);
		
	}
	
}
