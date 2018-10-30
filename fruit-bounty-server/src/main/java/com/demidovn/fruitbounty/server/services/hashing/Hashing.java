package com.demidovn.fruitbounty.server.services.hashing;

import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class Hashing {

  public static String getMd5Hash(String value) {
    try {
      byte[] bytesOfMessage = value.getBytes(StandardCharsets.UTF_8);
      MessageDigest md = MessageDigest.getInstance("MD5");

      byte[] hex = md.digest(bytesOfMessage);
      return String.format("%032x", new BigInteger(1, hex));
    } catch (NoSuchAlgorithmException e) {
      log.error("getMd5Hash error", e);
      throw new RuntimeException(e);
    }
  }

}
