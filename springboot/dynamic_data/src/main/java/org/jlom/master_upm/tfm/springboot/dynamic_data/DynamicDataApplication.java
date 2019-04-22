package org.jlom.master_upm.tfm.springboot.dynamic_data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
public class DynamicDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(DynamicDataApplication.class, args);
	}

}
