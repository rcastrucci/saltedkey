# Salted Key

[![Maven Central](https://img.shields.io/maven-central/v/com.rcastrucci.dev/SaltedKey.svg)](https://central.sonatype.com/artifact/com.rcastrucci.dev/SaltedKey/1.2.0)

A simple repository to salt a secret key and verify its authenticity. Developed to be used in mobile applications while communicating with a server side using an Api. Instead of sending an apikey straight on the request, SaltedKey can generate a temporary public key, valid for one time use and during a specific time frame, default time is set to 60 seconds. This public key can be sent on request and on server side SaltedKey can verify its authenticity. The Salt is based on time millis and uses the algorithm SHA-256 to create the temporary public key. The public key base will change every time it exceeds the time frame. This library can increase the API security. Even if the public key used on request is exposed, no one will be able to use it again! As it is a one time use only.

## Maven
    <dependency>
        <groupId>com.rcastrucci.dev</groupId>
        <artifactId>SaltedKey</artifactId>
        <version>1.2.0</version>
    </dependency>
    
## Gradle
    implementation 'com.rcastrucci.dev:SaltedKey:1.2.0'

## To Use
    // secret key
    String privateKey = "your_api_key";
    
    // client side
    String publicKey = Salt.getInstance().createSaltedKey(privateKey);
    
    // server side
    Boolean isValid = Salt.getInstance().verifySaltedKey(publicKey, privateKey);
    


### Create public key
###### This method will generate a publicKey valid for 1 minute.
    public createSaltedKey String (String privateKey)

### Verify public key
###### This method will verify if the public key match with privateKey.
    public verifySaltedKey boolean (String publicKey, String privateKey)
