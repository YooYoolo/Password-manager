package com.yotsume;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class PasswordManager {
    private final Map<User, Map<String,List<PasswordEntry>>> userPasswords;
    private final UserPasswordHistory history;

    public PasswordManager() {
        userPasswords = new LinkedHashMap<>();
        history = new UserPasswordHistory();
    }

    public void addUser(User user){
        if(!userPasswords.containsKey(user)){
            userPasswords.putIfAbsent(user, new LinkedHashMap<>());
        }
        else System.out.println("User already exists");

    }

    public void addService(User user, String service){
        if(userPasswords.containsKey(user) && !userPasswords.get(user).containsKey(service)){
            userPasswords.get(user).put(service, new ArrayList<>());
        } else System.out.println("Service already exists");
    }

    public void addPassword(User user, String service, String password) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (!userPasswords.containsKey(user) || !userPasswords.get(user).containsKey(service)) {
            System.out.println("User or service not found");
            return;
        }

        history.addToHistory(user, service, password);

        PasswordEntry passwordEntry = new PasswordEntry(password, user.getEncryptionKey());
        userPasswords.get(user).get(service).add(passwordEntry);
    }

    public void getAllPasswordEntries(User user) {
        if (!userPasswords.containsKey(user)) {
            System.out.println("User not found!");
            return;
        }

        System.out.println("\nUser: " + user.getUsername());
        Map<String, List<PasswordEntry>> services = userPasswords.get(user);

        for (Map.Entry<String, List<PasswordEntry>> entry : services.entrySet()) {
            System.out.println("  Service: " + entry.getKey());
            for (PasswordEntry password : entry.getValue()) {
                System.out.println("    Password: " + password.getPassword() +
                        " (created at: " + password.getCreatedAt() + ")");
            }
        }
    }

    public void getAllDecryptedPasswords(User user) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (!userPasswords.containsKey(user)) {
            System.out.println("User not found!");
            return;
        }

        System.out.println("\nUser: " + user.getUsername());
        Map<String, List<PasswordEntry>> services = userPasswords.get(user);

        for (Map.Entry<String, List<PasswordEntry>> entry : services.entrySet()) {
            System.out.println("  Service: " + entry.getKey());
            for (PasswordEntry password : entry.getValue()) {

                String decryptedPassword = password.getDecryptedPassword(user.getEncryptionKey());
                System.out.println("    Password: " + decryptedPassword +
                        " (created at: " + password.getCreatedAt() + ")");
            }
        }
    }

    public void updatePassword(User user, String service, String password) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if(!userPasswords.containsKey(user) || !userPasswords.get(user).containsKey(service)){
            System.out.println("User does not exist or service does not exist");
            return;
        }
        List<PasswordEntry> list = userPasswords.get(user).get(service);
        history.addToHistory(user, service, password);
        PasswordEntry newPassword = new PasswordEntry(password, user.getEncryptionKey());
        list.clear();
        list.add(newPassword);
    }

    public List<PasswordEntry> findPasswordByService(User user, String service) {
        if (!userPasswords.containsKey(user) || !userPasswords.get(user).containsKey(service)) {
            return Collections.emptyList();
        } else return new ArrayList<>(userPasswords.get(user).get(service));
    }

    public void deletePasswordEntry(User user, String service) {
        if (!userPasswords.containsKey(user) || !userPasswords.get(user).containsKey(service)) {
            System.out.println("User does not exist or service does not exist");
        } else {
            userPasswords.get(user).get(service).clear();
        }
    }

    public void restorePasswordFromHistory(User user, String service, int historyIndex) throws
            NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (!userPasswords.containsKey(user) || !userPasswords.get(user).containsKey(service)) {
            System.out.println("User does not exist or service does not exist");
            return;
        }

        List<PasswordEntry> userHistory = history.getHistory(user, service);

        if (userHistory.isEmpty()) {
            System.out.println("No history found for this service.");
            return;
        }
        if (historyIndex < 0 || historyIndex >= userHistory.size()) {
            System.out.println("Invalid history index.");
            return;
        }
        PasswordEntry passwordFromHistory = userHistory.get(historyIndex);
        String decryptedPassword = passwordFromHistory.getDecryptedPassword(user.getEncryptionKey());
        updatePassword(user, service, decryptedPassword);
        System.out.println("Password for " + user.getUsername()
                + "for service" + service
                + " restored from history.");
    }

    public void showHistory(User user, String service) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        history.showHistory(user, service);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<User, Map<String,List<PasswordEntry>>> userEntry : userPasswords.entrySet()) {
            User user = userEntry.getKey();
            sb.append("User: ").append(user.getUsername()).append("\n");

            Map<String,List<PasswordEntry>> service = userEntry.getValue();
            for (Map.Entry<String, List<PasswordEntry>> serviceEntry : service.entrySet()) {
                String serviceName = serviceEntry.getKey();
                sb.append("Service: ").append(serviceName).append("\n");

                List<PasswordEntry> passwords = serviceEntry.getValue();
                for (PasswordEntry password : passwords) {
                    sb.append("Password: ").append(password.getPassword())
                            .append("\n")
                            .append(" (created at: ")
                            .append(password.getCreatedAt())
                            .append(")\n");
                }
            }
        }
        return sb.toString();
    }


}
