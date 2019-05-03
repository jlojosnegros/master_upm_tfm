package org.jlom.master_upm.tfm.springboot.apigw.security.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ToString
@ConfigurationProperties("security")
@Configuration
public class SecurityConfig {
  private static final String SECRET = "SecretKeyToGenJWTs";
  private static final long EXPIRATION_TIME = 864_000_000; // 10 days
  private static final String TOKEN_PREFIX = "Bearer ";
  private static final String HEADER_STRING = "Authorization";
  public static final String SIGN_UP_URL = "/users/sign-up";

  private String secret = SECRET;
  private long expirationTime = EXPIRATION_TIME;
  private String tokenPrefix = TOKEN_PREFIX;
  private String headerName = HEADER_STRING;

}
