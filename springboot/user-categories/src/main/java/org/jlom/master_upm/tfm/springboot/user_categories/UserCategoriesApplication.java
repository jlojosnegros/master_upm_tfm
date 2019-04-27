package org.jlom.master_upm.tfm.springboot.user_categories;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;

@SpringBootApplication
@EnableDiscoveryClient
public class UserCategoriesApplication {
	public static void main(String[] args) {
		SpringApplication.run(UserCategoriesApplication.class, args);
	}

}