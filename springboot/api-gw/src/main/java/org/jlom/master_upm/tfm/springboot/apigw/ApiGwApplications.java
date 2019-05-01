package org.jlom.master_upm.tfm.springboot.apigw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGwApplications {

  public static void main(String[] args) {
    SpringApplication.run(ApiGwApplications.class, args);
  }

}