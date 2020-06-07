package il.wapp.app.validation;

import com.fasterxml.jackson.databind.*;
import il.wapp.app.domain.*;
import il.wapp.app.dto.user.*;
import il.wapp.app.enums.*;
import il.wapp.app.repository.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.nio.charset.*;
import java.util.*;

@Service
public class UserRegistrationValidator extends BaseEntityValidator {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    public User validate(UserRegisterDto dto) {

        List<AttrValidationError> errors = new ArrayList<>();
        if (checkRequired(dto.getEmail(), "email", errors)) {
            User user = userRepository.findByEmailAndStatusNot(dto.getEmail(), UserStatus.REMOVED);
            if (user != null) {
                if (UserStatus.ACTIVE.equals(user.getStatus())) {
                    errors.add(new AttrValidationError("email", "Email is not unique"));
                } else {
                    return user;
                }
            }
            checkMaxLength(dto.getFirstName(), "firstName", 100, errors);
            checkMaxLength(dto.getLastName(), "lastName", 100, errors);
        }
        parseErrors(errors, jacksonObjectMapper);
        return null;
    }

    public User checkUserRegisterLink(String registerLink, UserStatus status) {
        List<AttrValidationError> errors = new ArrayList<>();
        if (checkRequired(registerLink, "registerLink", errors)) {
            User user = userRepository.findByRegisterLink(registerLink);
            if (user == null) {
                errors.add(new AttrValidationError("registerLink", "registerLink is not register"));
            } else {
                return user;
            }
        }
        parseErrors(errors, jacksonObjectMapper);
        return null;
    }

    public User checkSetPassword(UserSetPasswordDto dto) {
        User user = checkUserRegisterLink(dto.getRegisterLink(), UserStatus.WAITING);
        List<AttrValidationError> errors = new ArrayList<>();
        if (checkRequired(dto.getPassword(), "password", errors)
                && checkRequired(dto.getConfirmPassword(), "confirmPassword", errors)) {
            if (!dto.getPassword().equals(dto.getConfirmPassword())) {
                errors.add(new AttrValidationError("confirmPassword", "Passwords mismatch"));
            }
        }

        parseErrors(errors, jacksonObjectMapper);
        return user;
    }

    public User login(UserLoginDto dto) {
        List<AttrValidationError> errors = new ArrayList<>();
        if (checkRequired(dto.getPassword(), "password", errors)
                && checkRequired(dto.getEmail(), "email", errors)) {
            User user = userRepository.findByEmailAndPasswordAndStatusNot(dto.getEmail()
                    , Base64.getEncoder().encodeToString(dto.getPassword()
                            .getBytes(StandardCharsets.UTF_8)), UserStatus.REMOVED);
            if (user != null) {
                if (!UserStatus.ACTIVE.equals(user.getStatus()) && !UserStatus.FIRST.equals(user.getStatus())) {
                    errors.add(new AttrValidationError("email", "User status is not ACTIVE or FIRST"));
                }
                return user;
            } else {
                errors.add(new AttrValidationError("email", "User not found"));
            }
        }

        parseErrors(errors, jacksonObjectMapper);
        return null;
    }
}
