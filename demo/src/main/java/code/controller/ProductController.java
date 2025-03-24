package code.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import code.DemoApplication;
import code.Util.ProductData;
import code.Util.SignupData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
//Importing required classes 
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.multipart.MultipartFile;

import code.DataStorage.Product;
import code.DataStorage.ProductManager;
import code.Sessions.SessionManager;
import code.Sessions.UserSession;
import code.Util.PostUtil;
import code.Util.Util;
import com.google.gson.JsonParser;

import static org.apache.juli.FileHandler.DEFAULT_BUFFER_SIZE;

//Annotation 
@Controller

public class ProductController {

    final FileSystemResource defaultImage = new FileSystemResource(new File(DemoApplication.PATH + "Storage/default.png"));

    @GetMapping(value = {"/product", "product.html"})
    public String getProduct(@RequestParam(value = "id") int id, Model model) {
        Product product = ProductManager.PRODUCTMANAGER.fromID(id);
        if (product != null) {
            model.addAttribute("product", product);
        }
        return "product";
    }

    @PostMapping(value = {"/products", "/cart"})
    public void placeOrder(@CookieValue(value = "sessionID") String sessionID, @RequestBody String payload) {
        HashMap<String, String> attributes = PostUtil.postData(payload);
        if (attributes.keySet().size() == 1) {

        }
    }

    @GetMapping(value = "/productlist", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Product> getProducts(@RequestParam(value = "start") int start, @RequestParam(value = "end") int end) {
        // Why bother with useless requests
        if (end < start)
            return new ArrayList<Product>();
        return ProductManager.PRODUCTMANAGER.returnProducts(start, end);

    }

    @GetMapping(value = "/productsearch", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Product> getProducts(@RequestParam(value = "search") String search, @RequestParam(value = "number") int number) {
        // Why bother with useless requests
        if (search == null || search.trim().isEmpty() || number < 0)
            return ProductManager.PRODUCTMANAGER.returnProducts(0, (number > 0 ? number : 0));
        return ProductManager.PRODUCTMANAGER.search(search, number);
    }

    @PostMapping(value = {"/Sell.html", "/Sell"}, headers = "content-type=multipart/form-data")
    public String createProduct(@CookieValue(value = "sessionID", required = false) String sessionID, @ModelAttribute ProductData data, Model model) {

        // If it is a valid session
        if (sessionID == null)
            return "SellFailure";
        UserSession session = SessionManager.SESSIONMANAGER.getSession(UUID.fromString(sessionID));
        if (session == null)
            return "SellFailure";

        model.addAttribute("validsuccess");

        String name = data.getName();
        String description = data.getDescription();
        Double price = data.getCost();
        MultipartFile[] files = data.getFiles();
        if (name != null && description != null && price != null && files != null) {
            String username = SessionManager.SESSIONMANAGER.getUsername(UUID.fromString(sessionID));
            if (username != null) {
                ProductManager.PRODUCTMANAGER.createProduct(data, username, files);
                return "redirect";
            } else
                System.out.println("Internal Error : username does not exist");
        }

        return "SellFailure";
    }

    @GetMapping({"/Sell", "/Sell.html"})
    public String signup(Model model) {
        model.addAttribute("data", new ProductData());
        return "Sell";
    }

    @RequestMapping(value = "/getImage", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource getUserFile(HttpServletResponse response, @RequestParam(value = "id") int id, @RequestParam(value = "number") int number) {

        File folder = new File(DemoApplication.PATH + "Storage/Products/" + id + "/");

        if (!folder.exists() || !folder.isDirectory())
            return defaultImage;
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null)
            return defaultImage;

        for (File file : listOfFiles) {
            if (file.exists() && file.getName().startsWith(number + "."))
                return new FileSystemResource(file);
        }

        //return new FileSystemResource(folder);

        return defaultImage;
    }

    @PostMapping("/upload")
    public void uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
        // Path fileNameAndPath =
        // ProductManager.PRODUCTMANAGER.getProductNextImagePath();
        // Files.write(fileNameAndPath, file.getBytes());
    }

}
