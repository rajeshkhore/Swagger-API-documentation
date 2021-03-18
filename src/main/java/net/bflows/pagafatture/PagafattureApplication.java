package net.bflows.pagafatture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableScheduling
public class PagafattureApplication {

	public static void main(String[] args) {
		SpringApplication.run(PagafattureApplication.class, args);
		System.out.println("Spring Boot Started");
	}

	@Configuration
	public class CorsConfig implements WebMvcConfigurer {

		@Override
		public void addCorsMappings(CorsRegistry registry) {

			registry.addMapping("/**").allowedOrigins("http://localhost:3000")
					.allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD").allowCredentials(true);
		}

	}
}
