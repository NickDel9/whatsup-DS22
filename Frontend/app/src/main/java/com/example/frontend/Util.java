package com.example.frontend;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {

    Util(){}

    public static BigInteger hash (String toBeHashed) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] messageDigest = md.digest(toBeHashed.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        return no;
    }

}
