package com.rcastrucci.dev;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Simple repository to create and verify salted keys.
 * @author dev.rcastrucci
 */
public class Salt {

    private final String salt;
    private final String spice;

    public Salt() {
        this.salt = generateSpices().get(0);
        this.spice = generateSpices().get(1);
    }

    /**
     * Generate a public key out of your private key this public key will be valid for about 1.5 minutes
     * @param privateKey your personal private key
     * @return a string format with a public key
     */
    public String createSaltedKey(String privateKey) {
        return hash(privateKey+salt);
    }

    /**
     * Verify if the public key match with a private key
     * @param publicKey a public key
     * @param privateKey a private key
     * @return boolean true if they match otherwise false
     */
    public boolean verifySaltedKey(String publicKey, String privateKey) {
        return (hash(privateKey+salt).equals(publicKey) || hash(privateKey+spice).equals(publicKey));
    }

    private String hash(final String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    private ArrayList<String> generateSpices() {
        Long ts = new Date().getTime();
        return new ArrayList<>(Arrays.asList(
                String.valueOf(ts).substring(0, String.valueOf(ts).length() - 5),
                String.valueOf(ts+100000).substring(0, String.valueOf(ts+100000).length() - 5)
        ));
    }

}