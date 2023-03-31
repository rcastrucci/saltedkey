# Salted Key

[![Maven Central](https://img.shields.io/maven-central/v/com.rcastrucci.dev/SaltedKey.svg)](https://central.sonatype.com/artifact/com.rcastrucci.dev/SaltedKey/1.0.1)

A simple repository to salt a secret key and verify it's authenticity. Developed to be used in mobile applications while comunicating with a server side using an Api. Instead of sending your apikey straight on the request, SaltedKey can generate a temporary public key, valid for one minute. This public key can be sent on request and on server side SaltedKey can verify it's authenticity and match with the apikey. The Salt is based on timemillis and uses the algorithm SHA-256 to create the public key. The public key will change every minute. This library can increase your API security leaving no room to leak your api secret key.

## Maven
    <dependency>
        <groupId>com.rcastrucci.dev</groupId>
        <artifactId>SaltedKey</artifactId>
        <version>1.0.1</version>
    </dependency>
    
## Gradle
    implementation 'com.rcastrucci.dev:SaltedKey:1.0.1'

## To Use
    // secret key
    String privateKey = "your_api_key";
    
    // client side
    String publicKey = new Salt().createSaltedKey(privateKey);
    
    // server side
    Boolean isValid = new Salt().verifySaltedKey(publicKey, privateKey);
    


### Create public key
###### This method will generate a publicKey valid for 1 minute.
    public createSaltedKey String (String privateKey)

### Verify public key
###### This method will verify if the public key match with privateKey.
    public verifySaltedKey boolean (String publicKey, String privateKey)
