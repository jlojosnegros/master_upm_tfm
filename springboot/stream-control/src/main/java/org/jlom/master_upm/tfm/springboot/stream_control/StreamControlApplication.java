package org.jlom.master_upm.tfm.springboot.stream_control;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class StreamControlApplication {
	public static void main(String[] args) {
		SpringApplication.run(StreamControlApplication.class, args);
	}

}
