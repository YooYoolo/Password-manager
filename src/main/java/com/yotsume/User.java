package com.yotsume;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

@Setter
@Getter
public class User {
    String username;
    SecretKey encryptionKey;

    public User(String username) throws NoSuchAlgorithmException {
        this.username = username;
        this.encryptionKey = generateSecretKey();
    }

    private SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        return keyGenerator.generateKey();
    }
}
