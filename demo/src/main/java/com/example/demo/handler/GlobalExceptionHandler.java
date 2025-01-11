package com.example.demo.handler;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

// 컨트롤 어드바이스은 모든 익셉션 발생 시 이 컨트롤러로 이동한다. 라는 기능이다.
@ControllerAdvice
@RestController
public class GlobalExceptionHandler{
	
	//익셉션 핸들러 어노테이션의 벨류값을 IllegalArgumentException.class로 주어야
	// IllegalArgumentException 발생 시 여기로 오게된다.
	@ExceptionHandler(value = IllegalArgumentException.class)
	public String handleArgumentException(IllegalArgumentException e) {
		return "<h1>"+e.getMessage()+"</h1>";
	}
}