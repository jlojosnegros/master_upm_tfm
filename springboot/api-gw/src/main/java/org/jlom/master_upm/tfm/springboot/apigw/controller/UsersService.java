package org.jlom.master_upm.tfm.springboot.apigw.controller;

import com.google.common.collect.Lists;
import org.jlom.master_upm.tfm.springboot.apigw.model.UserModel;
import org.jlom.master_upm.tfm.springboot.apigw.model.UsersRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UsersService implements UserDetailsService {

  private UsersRepository repository;
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  public UsersService(UsersRepository repository, BCryptPasswordEncoder bCryptPasswordEncoder) {
    this.repository = repository;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
  }


  public void newUser(UserModel user) {
    //@jlom todo comprobar que no hay otro usuario con el mismo username
    //@jlom todo Se le asigna el id automaticamente o tengo que asignarlo yo???
    user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
    repository.save(user);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    UserModel userModel = repository.findByUsername(username);
    if (null == userModel) {
      throw new UsernameNotFoundException(username);
    }
    return new User(userModel.getUsername(), userModel.getPassword(), Collections.emptyList());
  }

  public List<UserModel> listAllUsers() {
    Iterable<UserModel> all = repository.findAll();
    return Lists.newArrayList(all);
  }
}
