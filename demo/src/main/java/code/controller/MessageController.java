package code.controller;

import java.io.IOException;
import java.util.*;

// Importing required classes
import code.Util.ProductData;
import org.springframework.http.MediaType;
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
public class MessageController {

    private LinkedList<String> messages = new LinkedList<>();

    private int nextMessageID = 0;

    @PostMapping(value = "/MessageCenter.html")
    public @ResponseBody String submitMessage(@CookieValue(value = "sessionID", required = false) String sessionID, @RequestParam(value = "message") String message) throws IOException {

        if (message == null)
            return "-1";

        String name = "Guest";

        boolean validUser = false;

        // If it is a valid session
        if (sessionID != null) {
            String username = SessionManager.SESSIONMANAGER.getUsername(UUID.fromString(sessionID));
            if (username != null) {
                name = username;
                validUser = true;
            }
        }

        messages.addLast(name + "|" + validUser + "|" + nextMessageID++ + "|" + message);
        if (messages.size() > 50)
            messages.removeFirst();

        return "" + (nextMessageID - 1);
    }

    @GetMapping(value = "/getMessages", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<String> pollMessages() throws IOException {
        return messages;
    }


    @GetMapping({"/MessageCenter", "/MessageCenter.html"})
    public String messageCenter() {
        return "MessageCenter";
    }

}
