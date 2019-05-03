package org.jlom.master_upm.tfm.springboot.apigw.model;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UsersRepository extends CrudRepository<UserModel, Long> {
  List<UserModel> findByUsername(String username);
}
