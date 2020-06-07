package il.wapp.app.security;

import il.wapp.app.enums.UserRoles;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RealUser {
    UserRoles role() default UserRoles.OPERATOR;
}

