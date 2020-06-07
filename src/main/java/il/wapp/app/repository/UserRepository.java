package il.wapp.app.repository;

import il.wapp.app.domain.Corporation;
import il.wapp.app.domain.User;
import il.wapp.app.enums.UserStatus;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface UserRepository extends JpaRepository<User, Long> {

  User findByEmailAndStatusNot(String email, UserStatus status);

  User findByRegisterLink(String registerLink);

  User findByEmailAndPasswordAndStatusNot(String email, String password, UserStatus status);

  User findByTempToken(String tempToken);

  @Query(value = "SELECT usr FROM User usr " +
      "where usr.corporation = :corporation and usr.status = :status")
  List<User> findByCorporationAndActive(Corporation corporation, UserStatus status, Pageable pageable);

}
