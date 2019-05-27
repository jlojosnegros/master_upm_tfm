package org.jlom.master_upm.tfm.micronaut.apigw.model;


import io.micronaut.core.annotation.Introspected;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@Builder
@Introspected
public class UserModel {

  private static final Logger LOG = LoggerFactory.getLogger(UserModel.class);

  private String username; //Should be unique
  private String password;

  public UserModel() {
    LOG.error("jlom : empty CTOR:");
  }
  public UserModel(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    LOG.error("jlom : SET username:" + username);
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    LOG.error("jlom : SET password:" + password);
    this.password = password;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserModel userModel = (UserModel) o;
    return username.equals(userModel.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username);
  }

  @Override
  public String toString() {
    return "UserModel{" +
            "username='" + username + '\'' +
            ", password='" + password + '\'' +
            '}';
  }
}
