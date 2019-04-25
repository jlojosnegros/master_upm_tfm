package org.jlom.master_upm.tfm.springboot.dynamic_data.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Data
@RefreshScope
@Configuration
@ConfigurationProperties("testing")
public class PropertiesReading {
  private String prop;
  private String propTest;
}