package com.example.demo.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.RoleType;
import com.example.demo.model.Users;
import com.example.demo.repository.UserRepository;

@RestController
public class DummyControllerTest{
	
	@Autowired // 얘가 의존성주입이다 (DI)
	private UserRepository userRepository;
	
	@PostMapping("/dummy/join")
	public String join(Users user){
		
		user.setRole(RoleType.USER);
		userRepository.save(user);
		
		return "회원가입 성공";
	}
}