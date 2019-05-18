package org.jlom.master_upm.tfm.springboot.stream_control;

import org.jlom.master_upm.tfm.springboot.stream_control.controller.api.out.OutBoundNotifications;

import org.jlom.master_upm.tfm.springboot.stream_control.view.StartTime;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;

@SpringBootApplication
@EnableBinding(OutBoundNotifications.class)
public class StreamControlApplication {
	public static void main(String[] args) {
		StartTime.getInstance().setStartTime(System.currentTimeMillis());
		SpringApplication.run(StreamControlApplication.class, args);
	}

}
