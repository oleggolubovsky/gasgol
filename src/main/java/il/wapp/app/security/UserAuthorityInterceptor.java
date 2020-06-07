package il.wapp.app.security;

import il.wapp.app.constants.WappConstants;
import il.wapp.app.domain.User;
import il.wapp.app.enums.UserRoles;
import il.wapp.app.exception.AuthException;
import il.wapp.app.exception.PermissionException;
import il.wapp.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Slf4j
@Component
public class UserAuthorityInterceptor implements HandlerInterceptor {

    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (handler instanceof HandlerMethod) {
            RealUser realUser = ((HandlerMethod) handler).getMethodAnnotation(RealUser.class);

            if (null == realUser) {
                return true;
            }
            if (request.getHeader("authorization") == null) {
                throw new AuthException("Token is not define. Try authentication for generate token.");
            }
            User user = userService.currentUser();
            if (user == null) {
                throw new AuthException("Token exception. Can not find user");
            } else if (new Date(System.currentTimeMillis() - 3600 * 1000 * WappConstants.AUTH_PERIOD_HOURS)
                    .after(user.getLoginDate())) {
                throw new AuthException("Need authentication for generate token");
            }
            if (UserRoles.ADMIN.equals(realUser.role()) && !UserRoles.ADMIN.equals(user.getRole())) {
                    throw new PermissionException("Permission denied");
            }
            user.setLoginDate(new Date());
            userService.save(user);
        }
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    }

}

