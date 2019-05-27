package org.jlom.master_upm.tfm.micronaut.apigw.security;

import io.micronaut.security.authentication.AuthenticationFailed;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.UserDetails;
import io.reactivex.Flowable;
import org.jlom.master_upm.tfm.micronaut.apigw.controller.UsersService;
import org.jlom.master_upm.tfm.micronaut.apigw.model.UserModel;
import org.reactivestreams.Publisher;

import javax.inject.Singleton;
import java.util.ArrayList;

@Singleton
public class AuthenticationProviderUserPassword implements AuthenticationProvider {

  private final UsersService service;

  public AuthenticationProviderUserPassword(UsersService service) {
    this.service = service;
  }

  @Override
  public Publisher<AuthenticationResponse> authenticate(AuthenticationRequest authenticationRequest) {

    UserModel user = service.getUser(authenticationRequest.getIdentity().toString());
    if ( null == user || !user.getPassword().equals(authenticationRequest.getSecret().toString()) ) {
      return Flowable.just(new AuthenticationFailed());
    }
    return Flowable.just(new UserDetails((String) authenticationRequest.getIdentity(), new ArrayList<>()));
  }
}
