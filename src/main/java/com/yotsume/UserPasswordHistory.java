package com.yotsume;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class UserPasswordHistory {

    private final Map<User, Map<String, List<PasswordEntry>>> history = new LinkedHashMap<>();

    public void addToHistory(User user, String service, String password) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        PasswordEntry passwordEntry = new PasswordEntry(password, user.getEncryptionKey());
        history
                .computeIfAbsent(user, i -> new HashMap<>())
                .computeIfAbsent(service, i -> new ArrayList<>())
                .add(passwordEntry);
    }

    public List<PasswordEntry> getHistory(User user, String service) {
        return history.getOrDefault(user, Collections.emptyMap())
                .getOrDefault(service, Collections.emptyList());
    }

    public void showHistory(User user, String service) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        List<PasswordEntry> userHistory = getHistory(user, service);
        if (userHistory.isEmpty()) {
            System.out.println("No history for user " + user.getUsername() + ", service " + service);
            return;
        }
        System.out.println("\nHistory for user " + user.getUsername() + " service " + service + ":");
        for (PasswordEntry password : userHistory) {
            String decrypted = password.getDecryptedPassword(user.getEncryptionKey());
            System.out.println("Password: " + decrypted +
                    " (created at: " + password.getCreatedAt() + ")");
        }
    }
}
