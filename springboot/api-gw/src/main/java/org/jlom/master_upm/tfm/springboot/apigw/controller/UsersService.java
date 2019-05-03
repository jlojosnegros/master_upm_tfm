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

    List<UserModel> byUsername = repository.findByUsername(user.getUsername());
    if (! byUsername.isEmpty()) {
      throw new RuntimeException("error: user already exist username:" + user.getUsername() );
    }
    user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
    repository.save(user);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    List<UserModel> users = repository.findByUsername(username);

    if (users.isEmpty()) {
      throw new UsernameNotFoundException(username);
    }
    if (users.size() > 1) {
      throw new RuntimeException("FATAL error!! DB inconsistency!! username: " + username + " found:" + users);
    }
    var user = users.get(0);
    return new User(user.getUsername(), user.getPassword(), Collections.emptyList());
  }

  public List<UserModel> listAllUsers() {
    Iterable<UserModel> all = repository.findAll();
    return Lists.newArrayList(all);
  }
}
