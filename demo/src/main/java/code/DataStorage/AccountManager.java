package code.DataStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import code.Util.SignupData;
import com.google.gson.Gson;

import code.DemoApplication;

//This class loads and stores user-data in files
public class AccountManager {

    public static AccountManager ACCOUNTMANAGER;

    private File file;

    private ArrayList<Account> users = new ArrayList<>();

    private HashMap<Account, String> tempPasswords = new HashMap<>();

    public AccountManager() throws IOException {
        file = new File(DemoApplication.PATH + "Storage/latest");
        file.createNewFile();

        Scanner scan;
        try {
            scan = new Scanner(file);

            Gson g = new Gson();

            while (scan.hasNext()) {

                String line = scan.nextLine();

                if (line.equals(""))
                    continue;

                switch (line.split(" ")[0]) {
                    case "ADDUSER":
                        System.out.println(line.split(" ")[1]);
                        users.add(g.fromJson(line.split(" ")[1], Account.class));
                        break;
                    case "DELUSER":
                        users.remove(users.indexOf(line.split(" ")[1]));
                        break;
                }

            }
            for (Account account : users) {
                System.out.println("Account " + account.username + " initalized");
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void initalize() throws IOException {
        ACCOUNTMANAGER = new AccountManager();
    }

    public Account signIn(String username, String password) {
        for (Account account : users) {
            if (account.username.equalsIgnoreCase(username)) {
                if (tempPasswords.containsKey(account) && tempPasswords.get(account).equals(password)) {
                    tempPasswords.remove(account);
                    return account;
                }
                if (account.correctPassword(password))
                    return account;
                return null;
            }
        }
        return null;
    }

    public boolean addOneTimePassword(String email, String password) {
        for (Account account : users) {
            if (account.email.equalsIgnoreCase(email)) {
                tempPasswords.put(account, password);
                return true;
            }
        }
        return false;
    }

    public Account getAccount(String username) {
        for (Account account : users) {
            if (account.username.equalsIgnoreCase(username)) {
                return account;
            }
        }
        return null;
    }

    public boolean emailExists(String email) {
        for (Account account : users) {
            if (account.email.equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    public boolean createAccount(SignupData data) {
        for (Account u : users) {
            if (u.username.equals(data.getUsername()) || u.email.equals(data.getEmail()))
                return false;
        }

        Account account = new Account(data);
        users.add(account);
        writeUserToFile(account, file);

        try {
            Files.copy(new File(DemoApplication.PATH + "Storage/defaultPFP.png").toPath(), new File(DemoApplication.PATH + "Storage/ProfilePictures/" + account.getUsername() + ".png").toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Account " + account.username + " created");

        return true;
    }

    public void replaceAccount(Account account) {
        Account acc = null;
        for (Account a : users) {
            if (a.username.equals(account.username)) {
                removeUserFromFile(a, file);
                acc = a;
            }
        }
        users.remove(acc);

        users.add(account);
        writeUserToFile(account, file);

        try {
            Files.copy(new File(DemoApplication.PATH + "pfp.png").toPath(), new File(DemoApplication.PATH + "ProfilePictures/" + account.getUsername() + ".png").toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteAccount(String username) {
        Account acc = null;
        for (Account account : users) {
            if (account.username.equals(username)) {
                removeUserFromFile(account, file);
                acc = account;
                System.out.println("Account " + account.username + " deleted");
                return true;
            }
        }
        users.remove(acc);
        return false;
    }

    public void save() throws IOException {
        // Save current file
        File oldFile = new File(DemoApplication.PATH + "Storage/old");
        oldFile.delete();
        file.renameTo(oldFile);

        // Creates a new file and fills it
        file = new File("../Storage/latest");
        file.createNewFile();

        for (Account user : users) {
            writeUserToFile(user, file);
        }
    }

    // Private methods for writing to files

    private void writeUserToFile(Account user, File f) {

        try {
            Gson g = new Gson();
            FileWriter writer = new FileWriter(f, true);
            writer.write("ADDUSER " + g.toJson(user));
            writer.write(System.getProperty("line.separator")); // new line
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void removeUserFromFile(Account user, File f) {
        FileWriter writer;
        try {
            writer = new FileWriter(f, true);
            writer.write("DELUSER " + user.email);
            writer.write(System.getProperty("line.separator")); // new line
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
