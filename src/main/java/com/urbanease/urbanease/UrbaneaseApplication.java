package com.urbanease.urbanease;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {
    org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})
@EnableJpaRepositories("com.urbanease.repository")
@EntityScan("com.urbanease.model")
@ComponentScan("com.urbanease")
public class UrbaneaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(UrbaneaseApplication.class, args);
	}

}
