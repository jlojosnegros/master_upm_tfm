package org.jlom.master_upm.tfm.springboot.apigw.model;


import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("Users")
@Data
@Builder
public class UserModel {

  @Id
  Long userId;
  private String username; //Should be unique
  private String password;
}
