package com.example.demo.test;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.RoleType;
import com.example.demo.model.Users;
import com.example.demo.repository.UserRepository;

import jakarta.transaction.Transactional;
import net.bytebuddy.asm.Advice.OffsetMapping.Sort;

@RestController
public class DummyControllerTest{
	
	@Autowired // 얘가 의존성주입이다 (DI)
	private UserRepository userRepository;
	
	//삭제 DAO
	@DeleteMapping("/dummy/user/{id}")
	public String delete(@PathVariable int id) {
		try {			
			//Delete작업은 예외처리를 해줘야 안전하다.
			userRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			return "삭제 오류가 발생했습니다.";
		}
		
		return "삭제 되었습니다.";
	}
	
	//Update DAO
	//전달받을 매개변수 : Email, password
	@Transactional //트랜잭션 어노테이션을 설정해줘야 save() 함수 없이 업데이트가 가능하다.
	// 트랜색셔널을 설정해줘야 해당 메서드작업의 트랜잭션 처리를 시작한다는 뜻이다.
	@PutMapping("/dummy/user/{id}")
	public Users updateUser(@PathVariable int id, @RequestBody Users requestUser) {
		// URL경로의 값 : id
		// form태그에 들어있는 값 : email, password
		
		// 변경할 유저 id를 찾는다. (영속화 함.)
		Users user = userRepository.findById(id).orElseThrow(()->{
			return new IllegalArgumentException("수정에 실패하였습니다.");
		});
		
		// 찾은 user 객체에 수정한 값을 set해준다.
		// 영속화한 객체를 수정해서 트랜잭션이 커밋되면, DB와 영속컨테이너의 상태를 체크하여 update가 되어진다.
		user.setEmail(requestUser.getEmail());
		user.setPassword(requestUser.getPassword());
		
		// 변경된값을 보여줄필요 없으니 return은 null이다.
		return null;
	}


	// 모든값 Select하기
	@GetMapping("/dummy/user")
	public List<Users> list() {
		
		//모든 데이터를 리스트화해서 출력한다는 메서드이다.
		return userRepository.findAll();
	}

	// 한페이지당 n건에 데이터를 리턴받는다.
	// 주소요청을 http://localhost:8000/blog/dummy/user/page?page=1
	// 이라고하면 첫페이지만 출력해주게된다. 그후로는 page2,3.... 쭉쭉 나가짐.
	@GetMapping("/dummy/user/page")
	public Page<Users> pageList(@PageableDefault(size = 2, sort = "id", direction = Direction.DESC) Pageable pageable) {
		//@PageableDefault(size = 2, sort = "id", direction = Direction.DESC)
		/*
		 * 2개씩 페이징처리하고
		 * 정렬은 id로 기준으로하고
		 * direction은 오름차순 내림차순을 정의한다.
		*/
		
		//정의받은 page정보를 매개변수로 findAll하면 해당 페이징기법에 맞게 유저정보를 리스트해준다.
		Page<Users> user = userRepository.findAll(pageable);
	
		//리턴 시 Page타입으로하면 페이징정보도 같이 나가지는데
		//이게 싫다면 리턴타입을 List<>로 한다음 findAll(pageable).getContent로 가져와야한다.
		return user;
	}
	
	//{id} 주소로 파라미터를 전달받을 수 있다.
	// http://localhost:8000/blog/dummy/user/3
	@GetMapping("/dummy/user/{id}")
	public Users detail(@PathVariable int id) {
		//파라미터를 받을때는 @PathVariable 어노테이션을 사용하고
		//변수명은 GetMapping() 과 같은 이름으로 사용한다. {id} == id
		
		//select로 값을 가져올때는 optional이라는 옵션기능을 사용해야한다.
		//가져올 값이 null 일수도있고 아닐수도있고 다양한 변칙이있을수있으니까 사용하는거임.
		/*
		 * 1. get() : 절대 null일 경우가 없다.
		 * 2. orElseGet() : 데이터가 없을때 orElseGet 메서드를 진행한다. 
		 * 		익명클래스를 생성해서, null을 리턴해주는 메서드를 호출함. => 얘도 커스텀가능하다. 
		 * 		ex) new Users(); 이런식으로.
		 * 3. orElseThrow() : 얘도 똑같이 예외처리하는건데 throw를 타서 직접 예외상황을 커스텀할 수 있다.
		*/
		Users user = userRepository.findById(id).orElseGet(new Supplier<Users>() {
			@Override
			public Users get() {
				// TODO Auto-generated method stub
				return null;
			}
		});
		
		//리턴되는 이 시점에서 스프링의 메세지컨버터가 자바오브젝트를 JSON타입으로 변환하여 전달한다.
		//그래서 웹브라우저는 JSON데이터를 전달받아서 사용하게된다.
		return user;
	}
	
	@PostMapping("/dummy/join")
	public String join(Users user){
		
		user.setRole(RoleType.USER);
		userRepository.save(user);
		
		return "회원가입 성공";
	}
}