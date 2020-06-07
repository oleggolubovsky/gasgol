package il.wapp;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.*;
import org.springframework.web.client.*;
import org.springframework.web.servlet.config.annotation.*;

@SpringBootApplication
@EnableWebMvc
public class WappBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(WappBackendApplication.class, args);
	}

	@Bean
	RestTemplate restTemplate(){
		return new RestTemplate();
	}

}
