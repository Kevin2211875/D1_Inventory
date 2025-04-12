package com.uis.ejeMvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@Configuration
@EntityScan(basePackages = "com.uis.ejeMvc.model")
@EnableJpaRepositories(basePackages = "com.uis.ejeMvc.repository")
public class EjeMvcApplication {

	public static void main(String[] args) {
		SpringApplication.run(EjeMvcApplication.class, args);
	}

}
