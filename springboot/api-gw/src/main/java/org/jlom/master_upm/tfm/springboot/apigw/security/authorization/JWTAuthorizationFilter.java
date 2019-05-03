package org.jlom.master_upm.tfm.springboot.apigw.security.authorization;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.jlom.master_upm.tfm.springboot.apigw.security.config.SecurityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

  private static final Logger LOG = LoggerFactory.getLogger(JWTAuthorizationFilter.class);

  private SecurityConfig securityConfig;

  public JWTAuthorizationFilter (AuthenticationManager authenticationManager,
                                 SecurityConfig securityConfig) {
    super(authenticationManager);
    this.securityConfig = securityConfig;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain chain) throws IOException, ServletException {
    LOG.info("JWTAuthorizationFilter::doFilterInternal");
    // Obtener el token y comprobar minimamente que es el que tiene que ser
    String header = request.getHeader(securityConfig.getHeaderName());
    LOG.info("JWTAuthorizationFilter::doFilterInternal header="+ header);
    if ( (null == header) || (!header.startsWith(securityConfig.getTokenPrefix())) ) {
      LOG.error("JWTAuthorizationFilter::doFilterInternal error: header incorrect. header="+ header);
      chain.doFilter(request,response);
      return;
    }

    //Ontener los datos de autenticacion del token
    UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(request);

    // meter los datos de autenticacion en el contexto
    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    chain.doFilter(request,response);
  }

  private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
    LOG.info("JWTAuthorizationFilter::getAuthentication");
    String token = request.getHeader(securityConfig.getHeaderName());
    LOG.info("JWTAuthorizationFilter::getAuthentication token:" + token);
    if (null == token) {
      LOG.error("JWTAuthorizationFilter::getAuthentication error: null token:");
      return null;
    }

    // Construimos un verificador usando la firma
    // verificamos el token que hemos extraido del header
    // y obtenemos el subject que es el usuario para nosotros

    String user = JWT.require(Algorithm.HMAC512(securityConfig.getSecret().getBytes()))
            .build()
            .verify(token.replace(securityConfig.getTokenPrefix(),""))
            .getSubject();
    LOG.info("JWTAuthorizationFilter::getAuthentication user:" + user);

    if (null == user) {
      LOG.error("JWTAuthorizationFilter::getAuthentication error: null user");
      return null;
    }

    //al final retornamos un nuevo token con la informacion de usuario
    return new UsernamePasswordAuthenticationToken(user,null,new ArrayList<>());
  }
}
