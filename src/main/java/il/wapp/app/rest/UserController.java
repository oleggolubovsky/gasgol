package il.wapp.app.rest;

import il.wapp.app.dto.common.SearchDto;
import il.wapp.app.dto.user.UserDto;
import il.wapp.app.dto.user.UserEditDto;
import il.wapp.app.dto.user.UserForgotDto;
import il.wapp.app.dto.user.UserLoginDto;
import il.wapp.app.dto.user.UserRegisterDto;
import il.wapp.app.dto.user.UserSetPasswordDto;
import il.wapp.app.enums.UserRoles;
import il.wapp.app.security.RealUser;
import il.wapp.app.service.UserService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired
  private UserService userService;

  @RequestMapping(value = "confirm/{registerLink}", method = RequestMethod.GET)
  public UserRegisterDto confirm(@PathVariable("registerLink") String registerLink) {
    return userService.confirm(registerLink);
  }

  @RequestMapping(value = "/register", method = RequestMethod.POST)
  public UserRegisterDto register(@RequestBody UserRegisterDto userRegisterDto) {
    return userService.register(userRegisterDto);
  }

  @RequestMapping(value = "/invite", method = RequestMethod.POST)
  @RealUser
  public UserRegisterDto invite(@RequestBody UserRegisterDto userRegisterDto) {
    return userService.invite(userRegisterDto);
  }

  @RequestMapping(value = "/setPassword", method = RequestMethod.POST)
  public UserDto setPassword(@RequestBody UserSetPasswordDto userRegisterDto) {
    return userService.setPassword(userRegisterDto);
  }

  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public UserDto login(@RequestBody UserLoginDto dto, HttpServletResponse response,
      HttpServletRequest request) {
    return userService.login(dto, response, request);
  }

  @RequestMapping(value = "/email", method = RequestMethod.POST)
  public boolean email(@RequestBody UserRegisterDto user) {
    return userService.findByEmail(user.getEmail());
  }

  @RequestMapping(value = "/logout", method = RequestMethod.GET)
  @RealUser
  public void logout() {
    userService.logout();
  }

  @RequestMapping(value = "/all", method = RequestMethod.GET)
  @RealUser(role = UserRoles.ADMIN)
  public Page<UserDto> findAll(
      @RequestParam(value = "userId", required = false) Long userId,
      @RequestParam(value = "page", defaultValue = "0") int pageIndex,
      @RequestParam(value = "size", defaultValue = "10") int pageSize,
      @RequestParam(value = "sort", defaultValue = "email") String sort,
      @RequestParam(value = "direction", defaultValue = "asc", required = false) String direction
  ) {
    return userService.findAll(userId,
        PageRequest.of(pageIndex, pageSize,
            Sort.by("asc".equals(direction) ? Sort.Direction.ASC : Sort.Direction.DESC, sort)
        )
    );
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @RealUser(role = UserRoles.ADMIN)
  public UserDto getById(@PathVariable("id") Long id) {
    return userService.findById(id).toDto();
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  @RealUser(role = UserRoles.ADMIN)
  public UserDto delete(@PathVariable("id") Long id) {
    return userService.delete(id).toDto();
  }

  @RequestMapping(method = RequestMethod.PUT)
  @RealUser(role = UserRoles.ADMIN)
  public UserRegisterDto edit(@RequestBody UserEditDto dto) {
    return userService.edit(dto).toRegisterDto();
  }

  @GetMapping("/search")
  @RealUser
  public List<SearchDto> findByNameLike(
      @RequestParam(value = "search", defaultValue = "") String name,
      @RequestParam(value = "limit", defaultValue = "10") int limit
  ) {
    return userService.findByNameLike(name, limit);
  }

  @RequestMapping(value = "/forgot", method = RequestMethod.POST)
  public boolean email(@RequestBody UserForgotDto user) {
    return userService.forgot(user);
  }
}
