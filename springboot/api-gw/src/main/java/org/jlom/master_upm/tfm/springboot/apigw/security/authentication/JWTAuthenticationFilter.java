package org.jlom.master_upm.tfm.springboot.apigw.security.authentication;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jlom.master_upm.tfm.springboot.apigw.security.config.SecurityConfig;
import org.jlom.master_upm.tfm.springboot.apigw.view.api.dtos.ApplicationUser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private AuthenticationManager authenticationManager;
  private SecurityConfig securityConfig;

  public JWTAuthenticationFilter(AuthenticationManager authenticationManager, SecurityConfig securityConfig) {
    this.authenticationManager = authenticationManager;
    this.securityConfig = securityConfig;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
                                              HttpServletResponse response) throws AuthenticationException {
    try {
      ApplicationUser user = new ObjectMapper()
              .readValue(request.getInputStream(), ApplicationUser.class);

      return authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                      user.getUsername(),
                      user.getPassword(),
                      new ArrayList<>()
              )
      );
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request,
                                          HttpServletResponse response,
                                          FilterChain chain,
                                          Authentication authResult) throws IOException, ServletException {
    String token = JWT.create()
            .withSubject(((User) authResult.getPrincipal()).getUsername())
            .withExpiresAt(new Date(System.currentTimeMillis() + securityConfig.getExpirationTime()))
            .sign(Algorithm.HMAC512(securityConfig.getSecret().getBytes()));

    response.addHeader(securityConfig.getHeaderName(), securityConfig.getTokenPrefix() + token);
  }
}
