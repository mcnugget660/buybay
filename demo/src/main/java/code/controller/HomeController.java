package code.controller;

import code.DataStorage.Account;
import code.DataStorage.AccountManager;
import code.DataStorage.ProductManager;
import code.DemoApplication;
import code.Sessions.SessionManager;
import code.Sessions.UserSession;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

//Annotation 
@Controller
//Main class 
public class HomeController {

    final FileSystemResource defaultImage = new FileSystemResource(new File(DemoApplication.PATH + "Storage/default.png"));

    @GetMapping({"/Home", "/Home.html"})
    public String home(@CookieValue(value = "sessionID", required = false) String sessionID, Model model) {

        // If it is a valid session
        if (sessionID == null)
            return "redirect";

        if (SessionManager.SESSIONMANAGER.getSession(UUID.fromString(sessionID)) == null)
            return "redirect";

        String name = SessionManager.SESSIONMANAGER.getUsername(UUID.fromString(sessionID));
        model.addAttribute("user", AccountManager.ACCOUNTMANAGER.getAccount(name));
        model.addAttribute("products", ProductManager.PRODUCTMANAGER.getUserProducts(name));

        return "Profile";
    }

    @PostMapping(value = {"/Home", "/Home.html"}, headers = "content-type=multipart/form-data")
    public String home(@CookieValue(value = "sessionID", required = false) String sessionID, @RequestParam(value = "email", required = false) String email,
                       @RequestParam(value = "password", required = false) String password, @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
                       @RequestParam(value = "pfp", required = false) MultipartFile pfp, Model model) {
        // If it is a valid session
        if (sessionID == null)
            return "InvalidSession";
        UserSession session = SessionManager.SESSIONMANAGER.getSession(UUID.fromString(sessionID));
        if (session == null)
            return "InvalidSession";

        Account account = AccountManager.ACCOUNTMANAGER.getAccount(SessionManager.SESSIONMANAGER.getUsername(UUID.fromString(sessionID)));

        boolean changed = false;

        if (password != null && password.equals(confirmPassword)) {
            account = new Account(account.firstName, account.lastName, account.username, account.email, password);
            changed = true;
        }

        if (email != null && !email.equals(account.email)) {
            account.email = email;
            changed = true;
        }

        if (pfp != null && !pfp.isEmpty()) {
            try {
                Files.copy(pfp.getInputStream(), Paths.get(DemoApplication.PATH + "Storage/ProfilePictures/" + account.getUsername() + ".png"), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (changed)
            AccountManager.ACCOUNTMANAGER.replaceAccount(account);

        return "redirect";
    }

    @RequestMapping(value = "/getPfp", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource getUserFile(@RequestParam(value = "username") String username) {

        File file = new File(DemoApplication.PATH + "Storage/ProfilePictures/" + username + ".png");

        if (!file.exists())
            return defaultImage;

        return new FileSystemResource(file);
    }

}
