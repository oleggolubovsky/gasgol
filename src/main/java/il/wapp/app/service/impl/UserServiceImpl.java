package il.wapp.app.service.impl;

import il.wapp.app.constants.WappConstants;
import il.wapp.app.domain.Corporation;
import il.wapp.app.domain.User;
import il.wapp.app.dto.common.SearchDto;
import il.wapp.app.dto.user.*;
import il.wapp.app.enums.UserRoles;
import il.wapp.app.enums.UserStatus;
import il.wapp.app.exception.AttrValidationErrorException;
import il.wapp.app.exception.PermissionException;
import il.wapp.app.repository.CorporationRepository;
import il.wapp.app.repository.UserRepository;
import il.wapp.app.service.MailService;
import il.wapp.app.service.UserService;
import il.wapp.app.validation.UserRegistrationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private CorporationRepository corporationRepository;

  @Autowired
  private MailService mailService;

  @Autowired
  private HttpServletRequest request;

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private UserRegistrationValidator userRegistrationValidator;

  @Value("${register.link.confirm}")
  private String registerLink;

  @Override
  public User currentUser() {
    String tempToken = String.format(request.getHeader("authorization")
        .replaceAll("temp_token=", ""));
    return userRepository.findByTempToken(tempToken);
  }

  @Override
  public boolean findByEmail(String email) {
    return userRepository.findByEmailAndStatusNot(email, UserStatus.REMOVED) == null ? false : true;
  }

  @Override
  public User save(User user) {
    return userRepository.save(user);
  }

  @Override
  @Transactional
  public UserRegisterDto register(UserRegisterDto dto) {
    User newUser = userRegistrationValidator.validate(dto);
    Date current = new Date();
    if (newUser == null) {
      newUser = new User(dto.getEmail());
    }
    newUser.setFirstName(dto.getFirstName());
    newUser.setLastName(dto.getLastName());
    newUser.setStatus(UserStatus.valueOf(dto.getStatus()));
    newUser.setCreatedAt(current);
    newUser.setUpdatedAt(current);
    String regLink = UUID.randomUUID().toString();
    newUser.setRegisterLink(regLink);
    newUser.setCorporation(new Corporation(dto.getCorporation() != null
        ? dto.getCorporation() : "new Corporation", current));
    newUser.setRole(UserRoles.ADMIN);
    mailService.sendRegisterMail(newUser.getEmail(), registerLink + regLink,
        "Registration Confirmation", "Confirm registration by link: ");
    return userRepository.save(newUser).toRegisterDto();
  }

  @Override
  @Transactional
  public UserRegisterDto invite(UserRegisterDto dto) {
    User newUser = userRegistrationValidator.validate(dto);
    Date current = new Date();
    if (newUser == null) {
      newUser = new User(dto.getEmail());
    }
    newUser.setFirstName(dto.getFirstName());
    newUser.setLastName(dto.getLastName());
    newUser.setStatus(UserStatus.valueOf(dto.getStatus()));
    newUser.setCreatedAt(current);
    newUser.setUpdatedAt(current);
    String regLink = UUID.randomUUID().toString();
    newUser.setRegisterLink(regLink);
    newUser.setCorporation(currentUser().getCorporation());
    newUser.setRole(UserRoles.OPERATOR);
    mailService.sendRegisterMail(newUser.getEmail(), registerLink + regLink,
        "Registration Confirmation", "Confirm registration by link: ");
    return userRepository.save(newUser).toRegisterDto();
  }

  @Override
  public UserRegisterDto confirm(String registerLink) {
    User user = userRegistrationValidator.checkUserRegisterLink(registerLink, UserStatus.BLOCKED);
    user.setUpdatedAt(new Date());
    //user.setStatus(UserStatus.WAITING);
    userRepository.save(user);
    return user.toRegisterDto();
  }

  @Override
  public UserDto setPassword(UserSetPasswordDto dto) {
    User user = userRegistrationValidator.checkSetPassword(dto);
    user.setPassword(Base64.getEncoder().encodeToString(dto.getPassword()
        .getBytes(StandardCharsets.UTF_8)));
    user.setRegisterLink(null);
    user.setFirstName(dto.getFirstName());
    user.setLastName(dto.getLastName());
    user.setStatus(UserStatus.valueOf(dto.getStatus()));
    userRepository.save(user);
    return user.toDto();
  }

  @Override
  public UserDto login(UserLoginDto dto, HttpServletResponse response, HttpServletRequest request) {
    User user = userRegistrationValidator.login(dto);
    String tempToken = UUID.randomUUID().toString();
    user.setTempToken(tempToken);
    user.setLoginDate(new Date());
    userRepository.save(user);
    response.setHeader(WappConstants.TEMP_TOKEN, tempToken);
    UserDto userDto = user.toDto();
    userDto.setToken(tempToken);
    return userDto;
  }

  @Override
  public void logout() {
    User user = currentUser();
    user.setTempToken(null);
    userRepository.save(user);
  }

  @Override
  public Page<UserDto> findAll(Long userId, Pageable pageable) {
    List<User> users;
    if (userId != null) {
      users = Arrays.asList(findById(userId));
    } else {
      users = userRepository.findByCorporationAndActive(currentUser().getCorporation(), UserStatus.ACTIVE, pageable);
    }
    return new PageImpl(users
        .stream().map(c -> c.toDto()).collect(Collectors.toList()), pageable, users.size());
  }

  @Override
  public User findById(Long id) {
    Optional<User> user = userRepository.findById(id);
    if (!user.isPresent()) {
      throw new AttrValidationErrorException(String.format("Not found User with id = %s", id));
    }
    User currentUser = currentUser();
    if (!user.get().getCorporation().equals(currentUser.getCorporation())) {
      throw new PermissionException(String.format("Permission denied for userId = %s and user = %s",
          id, currentUser.getFirstName()
      ));
    }
    return user.get();
  }

  @Override
  @Transactional
  public User edit(UserEditDto dto) {
    User user = findById(dto.getId());
    if (currentUser().equals(user) && UserRoles.OPERATOR.equals(UserRoles.valueOf(dto.getRole()))) {
      throw new PermissionException("Admin cannot make himself a operator");
    }
    user.setRole(UserRoles.valueOf(dto.getRole()));
    user.setFirstName(dto.getFirstName());
    user.setLastName(dto.getLastName());
    user.setStatus(
        dto.getStatus() != null ? UserStatus.valueOf(dto.getStatus()) : user.getStatus());
    Corporation corporation = currentUser().getCorporation();
    if (dto.getCorporation() != null){
      corporation.setName(dto.getCorporation());
      user.setCorporation(corporation);
    }
    userRepository.save(user);
    return user;
  }

  @Override
  public User delete(Long id) {
    User user = findById(id);
    user.setStatus(UserStatus.REMOVED);
    userRepository.save(user);
    return user;
  }

  @Override
  public List<SearchDto> findByNameLike(String name, int limit) {
    List<Object[]> result =
        entityManager
            .createQuery("select u.id, CONCAT(u.firstName, ' ', u.lastName) AS name from User u " +
                "where (UPPER(u.firstName) like UPPER(:name) " +
                "or UPPER(u.lastName) like UPPER(:name)) " +
                "and u.corporation=:corporation order by u.firstName, u.lastName")
            .setParameter("name", "%" + name + "%")
            .setParameter("corporation", currentUser().getCorporation())
            .setMaxResults(limit)
            .getResultList();
    return result.stream()
        .map(object -> new SearchDto((Long) object[0], (String) object[1]))
        .collect(Collectors.toList());
  }

  @Override
  public boolean forgot(UserForgotDto dto) {
    User user = userRepository.findByEmailAndStatusNot(dto.getEmail(), UserStatus.REMOVED);
    if (user == null) {
      throw new AttrValidationErrorException(
          String.format("User with email = %s not found", dto.getEmail()));
    }
    user.setPassword(null);
    user.setTempToken(null);
    String regLink = UUID.randomUUID().toString();
    user.setRegisterLink(regLink);
    userRepository.save(user);
    mailService.sendRegisterMail(user.getEmail(), registerLink + regLink,
        "Password recovery", "To restore password follow by link: ");
    return true;
  }

}
