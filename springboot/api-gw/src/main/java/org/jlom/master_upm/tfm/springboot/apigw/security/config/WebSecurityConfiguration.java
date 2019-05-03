package org.jlom.master_upm.tfm.springboot.apigw.security.config;

import org.jlom.master_upm.tfm.springboot.apigw.controller.UsersService;
import org.jlom.master_upm.tfm.springboot.apigw.security.authentication.JWTAuthenticationFilter;
import org.jlom.master_upm.tfm.springboot.apigw.security.authorization.JWTAuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  private static final String BASE_APIGW_URI = "/apigw";
  private static final String CONTENT_BASE_URI = "/content";
  private static final String AUTHENTICATION_URL = BASE_APIGW_URI + SecurityConfig.SIGN_UP_URL;
  private static final String ACTUATOR_URL = "/actuator**";
  private static final String USER_LIST = "/apigw/users/all";

  private static final String CONTENT_ROOT_URI = BASE_APIGW_URI + CONTENT_BASE_URI;

  private UsersService usersService;
  private BCryptPasswordEncoder passwordEncoder;
  private SecurityConfig securityConfig;

  public WebSecurityConfiguration(UsersService usersService,
                                  BCryptPasswordEncoder passwordEncoder,
                                  SecurityConfig securityConfig) {
    this.usersService = usersService;
    this.passwordEncoder = passwordEncoder;
    this.securityConfig = securityConfig;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    List<String> permitAllEndpointList = Arrays.asList(
            AUTHENTICATION_URL,
            ACTUATOR_URL,
            CONTENT_ROOT_URI + "/content/soon",
            CONTENT_ROOT_URI + "/content/most-viewed/**"
    );

    http
            .cors().and().csrf().disable()
            .authorizeRequests()
            .antMatchers(permitAllEndpointList.toArray(new String[0])).permitAll()
            .antMatchers(HttpMethod.GET,USER_LIST).permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilter(new JWTAuthenticationFilter(authenticationManager(),securityConfig))
            .addFilter(new JWTAuthorizationFilter(authenticationManager(),securityConfig))
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(usersService)
            .passwordEncoder(passwordEncoder);
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
    return source;
  }
}
