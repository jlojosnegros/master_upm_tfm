package org.jlom.master_upm.tfm.springboot.apigw.utils;


import org.jlom.master_upm.tfm.springboot.apigw.model.UserModel;
import org.jlom.master_upm.tfm.springboot.apigw.view.api.dtos.ApplicationUser;

public class DtosTransformations {

  public static UserModel viewToService(ApplicationUser user) {
    return UserModel.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .build();
  }

  public static ApplicationUser serviceToView(UserModel user) {
    return ApplicationUser.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .build();
  }
}
