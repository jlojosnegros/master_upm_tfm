package org.jlom.master_upm.tfm.micronaut.apigw.utils;


import org.jlom.master_upm.tfm.micronaut.apigw.view.api.dtos.ApplicationUser;
import org.jlom.master_upm.tfm.micronaut.apigw.model.UserModel;

public class DtosTransformations {

  public static UserModel viewToService(ApplicationUser user) {
    return new UserModel(user.getUsername(),user.getPassword());
//    return UserModel.builder()
//            .username(user.getUsername())
//            .password(user.getPassword())
//            .build();
  }

  public static ApplicationUser serviceToView(UserModel user) {
    return ApplicationUser.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .build();
  }
}
