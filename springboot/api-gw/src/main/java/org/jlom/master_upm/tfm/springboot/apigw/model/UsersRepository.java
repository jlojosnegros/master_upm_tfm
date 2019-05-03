package org.jlom.master_upm.tfm.springboot.apigw.model;

import org.springframework.data.repository.CrudRepository;

public interface UsersRepository extends CrudRepository<UserModel, String> {
  UserModel findByUsername(String username);
}
