package org.jlom.master_upm.tfm.micronaut.apigw.view;


import com.fasterxml.jackson.core.JsonProcessingException;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import org.jlom.master_upm.tfm.micronaut.apigw.controller.UsersService;
import org.jlom.master_upm.tfm.micronaut.apigw.model.UsersRepository;
import org.jlom.master_upm.tfm.micronaut.apigw.utils.DtosTransformations;
import org.jlom.master_upm.tfm.micronaut.apigw.view.api.dtos.ApplicationUser;
import org.jlom.master_upm.tfm.micronaut.apigw.model.UserModel;

import java.util.List;
import java.util.stream.Collectors;

@Secured("isAnonymous()")
@Controller("/apigw/users")
public class ApiGwUsersRestServer {

  private UsersRepository applicationUsersRepository;
  private UsersService service;

  public ApiGwUsersRestServer(UsersRepository applicationUsersRepository,

                              UsersService service) {
    this.applicationUsersRepository = applicationUsersRepository;
    this.service = service;
  }

  @Post("/sign-up")
  public void signUp(@Body ApplicationUser user) throws JsonProcessingException {

    UserModel userModel = DtosTransformations.viewToService(user);
    service.newUser(userModel);
  }

  @Get("/all")
  public List<ApplicationUser> listAllUsers() {
    List<UserModel> userModels = service.listAllUsers();
    return userModels.stream()
            .map(DtosTransformations::serviceToView)
            .collect(Collectors.toList());
  }
}