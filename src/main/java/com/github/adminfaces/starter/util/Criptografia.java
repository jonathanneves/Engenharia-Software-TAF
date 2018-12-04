package com.github.adminfaces.starter.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Criptografia {

   public static String criptofragar(String cript) throws NoSuchAlgorithmException, UnsupportedEncodingException {
      MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
      byte messageDigest[] = algorithm.digest(cript.getBytes("UTF-8"));

      StringBuilder hexString = new StringBuilder();
      for (byte b : messageDigest) {
         hexString.append(String.format("%02X", 0xFF & b));
      }
      return hexString.toString();
   }


}