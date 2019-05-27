package org.jlom.master_upm.tfm.micronaut.apigw.controller;

import javax.inject.Singleton;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Singleton
public class MD5Crypter implements PasswordCrypter {

  private MessageDigest md5;

  public MD5Crypter() {
    try {
      this.md5 = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      this.md5 = null;
    }
  }

  @Override
  public String crypt(String str) {
    if (md5 == null) {
      throw new RuntimeException("Unable to initialize MessageDigest");
    }
    if (str == null || str.isEmpty()) {
      throw new IllegalArgumentException("String to encript cannot be null or zero length");
    }
    md5.reset();
    md5.update(str.getBytes());
    byte[] hash = md5.digest();
    StringBuilder hexString = new StringBuilder();
    for(byte value : hash) {
      if ((0xff & value) < 0x10) {
        hexString.append("0").append(Integer.toHexString((0xFF & value)));
      }
      else {
        hexString.append(Integer.toHexString(0xFF & value));
      }
    }
    return hexString.toString();
  }
}
