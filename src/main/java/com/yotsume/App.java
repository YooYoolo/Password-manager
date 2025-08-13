package com.yotsume;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class App
{
    public static void main( String[] args ) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        User user1 = new User("yotsume");
        PasswordManager passwordManager = new PasswordManager();
        User user2 = new User("Mike");


        passwordManager.addUser(user1);
        passwordManager.addService(user1, "google.com");
        passwordManager.addPassword(user1, "google.com", "12345");

        passwordManager.addUser(user2);
        passwordManager.addService(user2, "discord.com");
        passwordManager.addPassword(user2, "discord.com", "66666");

        passwordManager.updatePassword(user1, "google.com", "0000");
        passwordManager.updatePassword(user1, "google.com", "1111");
        passwordManager.updatePassword(user2, "discord.com", "9966");
        passwordManager.restorePasswordFromHistory(user1, "google.com", 1);

        passwordManager.getAllDecryptedPasswords(user1);
        passwordManager.getAllDecryptedPasswords(user2);

        passwordManager.showHistory(user1, "google.com");
        passwordManager.showHistory(user2, "discord.com");

    }
}
