package code.controller;

import code.DataStorage.AccountManager;
import code.Sessions.SessionManager;
import code.Sessions.UserSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

//Annotation
@Controller
//Main class
public class IndexController {

    @RequestMapping({"/index", "/index.html"})
    public String index(@CookieValue(value = "sessionID", required = false) String sessionID, Model model) {
        // If it is a valid session
        if (sessionID != null) {
            UserSession session = SessionManager.SESSIONMANAGER.getSession(UUID.fromString(sessionID));
            if (session != null) {
                model.addAttribute("validSession", true);
                String name = SessionManager.SESSIONMANAGER.getUsername(UUID.fromString(sessionID));
                model.addAttribute("user", AccountManager.ACCOUNTMANAGER.getAccount(name));
            }
        }
        return "index";
    }

    @RequestMapping({"/About", "/About.html"})
    public String about(@CookieValue(value = "sessionID", required = false) String sessionID, Model model) {
        // If it is a valid session
        if (sessionID != null) {
            UserSession session = SessionManager.SESSIONMANAGER.getSession(UUID.fromString(sessionID));
            if (session != null)
                model.addAttribute("validSession", true);
        }
        return "About";
    }

}
