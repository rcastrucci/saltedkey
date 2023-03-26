# Salted Key

A simple repository to create and verify salted keys.

The library has two public methods:

### Create public key
###### This method will generate a publicKey valid for 1 minute. This key can be verified by the other method called verifySaltedKey.
    public createSaltedKey String (String privateKey)

### Verify public key
###### This method will verify if the public key match with privateKey.
    public verifySaltedKey boolean (String publicKey, String privateKey)
