package code.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

// Importing required classes 
import code.Util.ProductData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import code.DataStorage.Account;
import code.DataStorage.AccountManager;
import code.DataStorage.ProductManager;
import code.Sessions.SessionManager;
import code.Sessions.UserSession;
import code.Util.PostUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

// Annotation
@Controller
// Main class
public class LoginController {

    @PostMapping({"/login", "/login.html"})
    public String createAccount(@RequestBody String payload, HttpServletResponse response, Model model) throws IOException {
//Correctly sets the cookie make sure to ensure atrributes exist before using them
        HashMap<String, String> attributes = PostUtil.postData(payload);

        if (attributes.keySet().size() == 2) {

            String username = attributes.get("username");
            String password = attributes.get("password");

            Account account = AccountManager.ACCOUNTMANAGER.signIn(username, password);
            if (account != null) {
                UserSession session = new UserSession();
                SessionManager.SESSIONMANAGER.openSession(session, account.getUsername());
                // Send session to user
                Cookie cookie = new Cookie("sessionID", session.getUUID());
                cookie.setHttpOnly(true); // Only accessed by the server
                cookie.setPath("/"); // global cookie accessible everywhere
                //cookie.setSecure(true); // only sent over https
                response.addCookie(cookie);

                return "redirect";
            }

            model.addAttribute("nonexistentAccount", true);

            // @CookieValue(value = "username", defaultValue = "Atta"
        }
        return "login";
    }

    @RequestMapping("/signout")
    public String signup(@CookieValue(value = "sessionID", required = false) String sessionID) {

        if (sessionID == null)
            return "index";

        // Remove Session
        SessionManager.SESSIONMANAGER.endSession(UUID.fromString(sessionID));

        return "redirect";
    }

    @RequestMapping({"/login", "/login.html"})
    public String signup() {
        return "login";
    }
}
