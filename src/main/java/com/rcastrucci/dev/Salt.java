package com.rcastrucci.dev;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


/**
 * A simple repository to salt a secret key and verify its authenticity.
 * Developed to be used in mobile applications while communicating with a server side using an Api.
 * Instead of sending an apikey straight on the request, SaltedKey can generate a temporary public key,
 * valid for one time use and during a specific time frame, default time is set to 60 seconds.
 * This public key can be sent on request and on server side SaltedKey can verify its authenticity.
 * The Salt is based on time millis and uses the algorithm SHA-256 to create the temporary public key.
 * The public key base will change every time it exceeds the time frame.
 * This library can increase the API security. Even if the public key used on request is exposed, no one
 * will be able to use it again! As it is a one time use only.
 * @author dev.rcastrucci
 * @version 1.2.0
 */
public class Salt {

    private String salt;
    private String spice;
    private static Salt instance;
    private final int HASH_LENGTH;
    public Long TIME_FRAME = 60000L;
    private final List<PublicKey> keys = new ArrayList<>();

    /**
     * Private Constructor
     */
    private Salt() {
        HASH_LENGTH = hash("").length();
    }

    /**
     * Salt instance method
     * @return a unique instance of this object
     */
    public static Salt getInstance() {
        if (instance == null) instance = new Salt();
        return instance;
    }

    /**
     * Generate a salted key out of the secret key valid during the time frame
     * @param privateKey your secret private key
     * @return a salted key valid during the time frame
     */
    public String createSaltedKey(String privateKey) {
        Long ts = generateSpices();
        return hash(privateKey+salt)+ts;
    }

    /**
     * Retrieves the key time
     * @param publicKey a String type containing a key
     * @return if invalid key will return null otherwise returns the key time
     */
    private Long getKeyTime(String publicKey) {
        if (publicKey.length() > HASH_LENGTH) {
            String ts = publicKey.substring(HASH_LENGTH);
            if (ts.matches("^[0-9]+")) {
                return Long.parseLong(ts);
            }
        }
        return null;
    }

    /**
     * Verify if the key matches with a private key
     * @param key is the key to be verified
     * @param privateKey is the secret key
     * @return boolean true if they match during the time frame otherwise false
     */
    public boolean verifySaltedKey(String key, String privateKey) {
        cleanKeys();
        Long ts = generateSpices();
        PublicKey publicKey = new PublicKey(key, getKeyTime(key));
        PublicKey publicSalt = new PublicKey(hash(privateKey+salt), ts);
        PublicKey publicSpice = new PublicKey(hash(privateKey+spice), ts);
        if ((publicSalt.getKey().equals(publicKey.getKey()) || publicSpice.getKey().equals(publicKey.getKey())) &&
            ((publicSalt.time-publicKey.time < TIME_FRAME) || (publicSpice.time-publicKey.time < TIME_FRAME)) &&
            (!keys.contains(publicKey))) {
            keys.add(publicKey);
            return true;
        }
        return false;
    }

    /**
     * Remove expired keys from the used keys list
     */
    private void cleanKeys() {
        Long ts = new Date().getTime();
        List<PublicKey> expiredKeys = new ArrayList<>();
        for (PublicKey key : keys) {
            if ((ts - key.getTime()) > (TIME_FRAME*2)) {
                expiredKeys.add(key);
            }
        }
        keys.removeAll(expiredKeys);
    }

    /**
     * Generate a 64-bit hash using algorithm SHA-256
     * @param base a string to generate the hash
     * @return SHA-256 Hash
     */
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

    /**
     * Generate spices
     * @return a Long with the current time millis used to generate spices
     */
    private Long generateSpices() {
        Long ts = new Date().getTime()+new Random().nextInt(100);
        this.salt = String.valueOf(ts).substring(0, String.valueOf(ts).length() - 5);
        this.spice = String.valueOf(ts+TIME_FRAME).substring(0, String.valueOf(ts+TIME_FRAME).length() - 5);
        return ts;
    }

    /**
     * Public key model object
     */
    private class PublicKey implements Serializable {
        private String key;
        private Long time;

        /** Public Key Constructor
         * @param key is the key generated with salt or spice
         * @param time corresponds to the time when the key was generated
         */
        private PublicKey(String key, Long time) {
            this.key = key;
            this.time = time;
        }

        /**
         * Retrieve the public key string
         * @return a String type object
         */
        public String getKey() {
            if (this.key.length() >= HASH_LENGTH)
                return this.key.substring(0, HASH_LENGTH);
            else return this.key;
        }

        /**
         * Retrieve the key time
         * @return a Long type object
         */
        public Long getTime() {
            return this.time;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            PublicKey that = (PublicKey) object;
            return key.equals(that.key) && time.equals(that.time);
        }

    }

}
