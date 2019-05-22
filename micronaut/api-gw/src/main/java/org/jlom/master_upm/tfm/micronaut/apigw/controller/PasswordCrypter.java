package org.jlom.master_upm.tfm.micronaut.apigw.controller;

public interface PasswordCrypter {
  String crypt(String password);
}
