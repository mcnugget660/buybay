package code.controller;

import code.DataStorage.Product;
import code.DataStorage.ProductManager;
import code.Sessions.SessionManager;
import code.Sessions.UserSession;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//Annotation
@Controller
public class CartController {

    @GetMapping(value = "/getCart", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Product> getCart(@CookieValue(value = "sessionID") String sessionID) {

        ArrayList<Product> list = null;

        // If it is a valid session
        if (sessionID == null)
            return list;
        UserSession session = SessionManager.SESSIONMANAGER.getSession(UUID.fromString(sessionID));
        if (session == null)
            return list;

        return ProductManager.PRODUCTMANAGER.getCart(SessionManager.SESSIONMANAGER.getUsername(UUID.fromString(sessionID)));
    }

    @PostMapping(value = "/addCart")
    public void addToCart(@CookieValue(value = "sessionID") String sessionID, @RequestParam(value = "id") int id) {

        ArrayList<Product> list = null;

        // If it is a valid session
        if (sessionID == null)
            return;
        UserSession session = SessionManager.SESSIONMANAGER.getSession(UUID.fromString(sessionID));
        if (session == null)
            return;

        ProductManager.PRODUCTMANAGER.addToCart(SessionManager.SESSIONMANAGER.getUsername(UUID.fromString(sessionID)), id);
    }

    @PostMapping(value = "/removeFromCart")
    public void removeFromCart(@CookieValue(value = "sessionID") String sessionID, @RequestParam(value = "id") int id) {

        ArrayList<Product> list = null;

        // If it is a valid session
        if (sessionID == null)
            return;
        UserSession session = SessionManager.SESSIONMANAGER.getSession(UUID.fromString(sessionID));
        if (session == null)
            return;

        ProductManager.PRODUCTMANAGER.removeFromCart(SessionManager.SESSIONMANAGER.getUsername(UUID.fromString(sessionID)), id);
    }

}
