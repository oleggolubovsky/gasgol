package il.wapp.app.configuration;

import il.wapp.app.security.*;
import il.wapp.app.service.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.view.*;

@Configuration
@Slf4j
public class WebConfiguration extends DelegatingWebMvcConfiguration {

	@Autowired
	private UserAuthorityInterceptor userAuthorityInterceptor;

	@Autowired
	private UserService userService;

	@Bean
	public InternalResourceViewResolver defaultViewResolver() {
		return new InternalResourceViewResolver();
	}

	/**Add interceptor configuration, do not use xml file**/
	@Override
	protected void addInterceptors(InterceptorRegistry registry) {
		userAuthorityInterceptor.setUserService(userService);
		registry.addInterceptor(userAuthorityInterceptor).addPathPatterns("/**");
		log.info("Configure Interceptor.....");
		super.addInterceptors(registry);
	}

	@Override
	protected void configureViewResolvers(ViewResolverRegistry registry) {
		registry.viewResolver(defaultViewResolver());
		super.configureViewResolvers(registry);
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
	    registry.addMapping("/**")
			.allowedMethods("*")
			//.allowedOrigins("localhost:9999")
			.allowCredentials(true);

	}
}

