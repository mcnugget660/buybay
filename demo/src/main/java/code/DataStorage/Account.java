package code.DataStorage;

import code.Util.SignupData;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Account {
    private static final String ADDITION = "'(t:(Sb!w'PLt#abZB@p";
    public String firstName;
    public String lastName;
    public String email;
    public String username;
    public byte[] passwordHash;
    private byte[] salt;

    public Account(String firstName, String lastName, String username, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;

        SecureRandom random = new SecureRandom();
        salt = new byte[16];

        random.nextBytes(salt);

        passwordHash = hash(password + ADDITION.substring(0, 20 - password.length()));
    }

    public Account(SignupData data) {
        this(data.getFirstName(), data.getLastName(), data.getUsername(), data.getEmail(), data.getPassword());
    }

    public boolean correctPassword(String password) {
        byte[] hash = hash(password + ADDITION.substring(0, 20 - password.length()));
        System.out.println("B");
        for (int i = 0; i < hash.length; i++) {
            if (hash[i] != passwordHash[i])
                return false;
        }
        return true;
    }

    // String s must be 20 Characters
    private byte[] hash(String s) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");

            md.update(salt);

            return md.digest(s.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
