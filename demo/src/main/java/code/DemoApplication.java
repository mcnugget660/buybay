package code;

import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

import jakarta.mail.MessagingException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import code.DataStorage.AccountManager;
import code.DataStorage.ProductManager;
import code.DataStorage.Settings;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class DemoApplication {

    // Data will be stored in parent folder at jar location
    public static final String PATH = "../";

    public static final EmailManager emailManager = new EmailManager();

    public static void main(String[] args) throws IOException {
        SpringApplication application = new SpringApplication(DemoApplication.class);
        application.run(args);
        Runtime.getRuntime().addShutdownHook(new Thread(DemoApplication::exit));
    }

    @PostConstruct
    public static void setUp() throws IOException {
        // Create directories if they do not exist
        new File(PATH + "Storage").mkdirs();
        new File(PATH + "Storage/Products").mkdirs();
        new File(PATH + "Storage/ProfilePictures").mkdirs();
        new File(PATH + "logs").mkdirs();

        Settings.loadSettings();

        ProductManager.initalize(); // MUST BE FIRST

        AccountManager.initalize();

    }

    public static void exit() {
        ProductManager.PRODUCTMANAGER.onClose();
    }

    public static boolean isValidPath(String path) {
        try {
            Paths.get(path);
        } catch (InvalidPathException | NullPointerException ex) {
            return false;
        }
        return true;
    }
}