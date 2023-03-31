package com.rcastrucci.dev;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * A simple repository to salt a secret key and verify it's authenticity.
 * Developed to be used in mobile applications while comunicating with a server side using an Api.
 * Instead of sending your apikey straight on the request, SaltedKey can generate a temporary public key,
 * valid for one minute. This public key can be sent on request and on server side SaltedKey can verify
 * it's authenticity and match with the apikey. The Salt is based on timemillis and uses the algorithm
 * SHA-256 to create the public key. The public key will change every minute. This library can increase
 * your API security leaving no room to leak your api secret key.
 * @author dev.rcastrucci
 */
public class Salt {

    private final String salt;
    private final String spice;

    /**
     * Constructor
     */
    public Salt() {
        this.salt = generateSpices().get(0);
        this.spice = generateSpices().get(1);
    }

    /**
     * Generate a public key out of your private key this public key will be valid for about 1.5 minutes
     * @param privateKey your secret private key
     * @return a string format with a public key
     */
    public String createSaltedKey(String privateKey) {
        return hash(privateKey+salt);
    }

    /**
     * Verify if the public key match with a private key
     * @param publicKey is the key to be verified and check if match with your private key
     * @param privateKey is your secret private key
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
