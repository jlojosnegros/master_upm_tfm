package org.jlom.master_upm.tfm.springboot.recommendations;

import org.jlom.master_upm.tfm.springboot.recommendations.view.api.InBoundNotifications;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;

@SpringBootApplication
@EnableBinding(InBoundNotifications.class)
public class RecommendationsApplication {
	public static void main(String[] args) {
		SpringApplication.run(RecommendationsApplication.class, args);
	}

}
