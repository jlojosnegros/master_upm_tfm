package org.jlom.master_upm.tfm.springboot.apigw.model;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.Objects;

@RedisHash("UserModel")
public class UserModel {

  private static final Logger LOG = LoggerFactory.getLogger(UserModel.class);

  @Id
  private Long userId;

  @Indexed
  private String username; //Should be unique
  private String password;

  public UserModel() {
    LOG.error("jlom : empty CTOR:");
  }
  public UserModel(Long userId, String username, String password) {
    LOG.error("jlom : CTOR userId:" + userId );
    this.userId = userId;
    this.username = username;
    this.password = password;
  }

  public UserModel(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public Long getUserId() {
    LOG.error("jlom : GET userId:" + userId );
    return userId;
  }

  public void setUserId(Long userId) {
    LOG.error("jlom : SET userId:" + userId );
    this.userId = userId;
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
    return userId.equals(userModel.userId) &&
            username.equals(userModel.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, username);
  }

  @Override
  public String toString() {
    return "UserModel{" +
            "userId=" + userId +
            ", username='" + username + '\'' +
            ", password='" + password + '\'' +
            '}';
  }
}
