package org.jlom.master_upm.tfm.springboot.apigw.view;


import org.jlom.master_upm.tfm.springboot.apigw.controller.UsersService;
import org.jlom.master_upm.tfm.springboot.apigw.model.UserModel;
import org.jlom.master_upm.tfm.springboot.apigw.model.UsersRepository;
import org.jlom.master_upm.tfm.springboot.apigw.utils.DtosTransformations;
import org.jlom.master_upm.tfm.springboot.apigw.view.api.dtos.ApplicationUser;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/apigw/users")
public class ApiGwUsersRestServer {

  private UsersRepository applicationUsersRepository;
  private BCryptPasswordEncoder bCryptPasswordEncoder;
  private UsersService service;

  public ApiGwUsersRestServer(UsersRepository applicationUsersRepository,
                              BCryptPasswordEncoder bCryptPasswordEncoder,
                              UsersService service) {
    this.applicationUsersRepository = applicationUsersRepository;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.service = service;
  }

  @PostMapping("/sign-up")
  public void signUp(@RequestBody ApplicationUser user) {

    UserModel userModel = DtosTransformations.viewToService(user);
    service.newUser(userModel);
  }

  @GetMapping("/all")
  public List<ApplicationUser> listAllUsers() {
    List<UserModel> userModels = service.listAllUsers();
    return userModels.stream()
            .map(DtosTransformations::serviceToView)
            .collect(Collectors.toList());
  }
}