package org.jlom.master_upm.tfm.micronaut.apigw.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.jlom.master_upm.tfm.micronaut.apigw.model.UserModel;
import org.jlom.master_upm.tfm.micronaut.apigw.model.UsersRepository;

import javax.inject.Singleton;
import java.util.List;


@Singleton
@Slf4j
public class UsersService {

  private UsersRepository repository;
  private final PasswordCrypter passwordCrypter;


  public UsersService(UsersRepository repository, PasswordCrypter passwordCrypter) {
    this.repository = repository;
    this.passwordCrypter = passwordCrypter;
  }


  public void newUser(UserModel user) throws JsonProcessingException {
    UserModel byUsername = repository.findByUsername(user.getUsername());
    if (null != byUsername) {
      throw new RuntimeException("error: user already exist username:" + user.getUsername() );
    }

    log.error("password:" + user.getPassword());
    log.error("md5paswd:" + passwordCrypter.crypt(user.getPassword()));

    user.setPassword(passwordCrypter.crypt(user.getPassword()));
    repository.save(user);
  }

  public List<UserModel> listAllUsers() {
    Iterable<UserModel> all = repository.findAll();
    return Lists.newArrayList(all);
  }


  public UserModel getUser(String username) {
    return repository.findByUsername(username);
  }

}
