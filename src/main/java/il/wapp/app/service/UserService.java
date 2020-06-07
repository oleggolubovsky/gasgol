package il.wapp.app.service;

import il.wapp.app.domain.*;
import il.wapp.app.dto.common.SearchDto;
import il.wapp.app.dto.user.*;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.*;

import javax.servlet.http.*;
import java.util.List;

@Repository
public interface UserService {

	User currentUser();

	boolean findByEmail(String email);

	User save(User user);

	UserRegisterDto register(UserRegisterDto dto);

	UserRegisterDto invite(UserRegisterDto dto);

	UserRegisterDto confirm(String registerLink);

	UserDto setPassword(UserSetPasswordDto userSetPasswordDto);

	UserDto login(UserLoginDto dto, HttpServletResponse response, HttpServletRequest request);

	void logout();

    Page<UserDto> findAll(Long userId, Pageable pageable);

	User findById(Long id);

	User edit(UserEditDto dto);

	List<SearchDto> findByNameLike(String name, int limit);

	User delete(Long id);

	boolean forgot(UserForgotDto dto);

}
