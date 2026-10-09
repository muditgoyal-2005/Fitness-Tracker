package com.fitness.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;

public final class PasswordUtil {
    private static final SecureRandom RANDOM=new SecureRandom();
    private PasswordUtil(){ }
    public static String hash(String password){byte[] salt=new byte[16];RANDOM.nextBytes(salt);String saltHex=HexFormat.of().formatHex(salt);return "sha256$"+saltHex+"$"+digest(saltHex,password);}
    public static boolean verify(String password,String encoded){
        if(encoded==null)return false;String[] parts=encoded.split("\\$",-1);if(parts.length!=3||!"sha256".equals(parts[0]))return false;
        return MessageDigest.isEqual(digest(parts[1],password).getBytes(StandardCharsets.US_ASCII),parts[2].getBytes(StandardCharsets.US_ASCII));
    }
    private static String digest(String salt,String password){try{MessageDigest md=MessageDigest.getInstance("SHA-256");return HexFormat.of().formatHex(md.digest((salt+password).getBytes(StandardCharsets.UTF_8)));}catch(Exception e){throw new IllegalStateException("SHA-256 is unavailable",e);}}
}