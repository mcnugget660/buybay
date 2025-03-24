package code.controller;

import code.DataStorage.AccountManager;
import code.DemoApplication;
import code.EmailManager;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

//Annotation
@Controller
public class ResetPasswordController {

    @PostMapping(value = {"/ForgotPassword", "/ForgotPassword.html"})
    public String submit(@RequestParam(value = "email", required = false) String email,
                         @RequestParam(value = "confirmEmail", required = false) String confirmEmail, Model model) {
        if (email != null && email.equals(confirmEmail)) {
            if (AccountManager.ACCOUNTMANAGER.emailExists(email)) {
                try {
                    System.out.println(email);
                    String password = Integer.toString((int) (Math.random() * 10000000));
                    AccountManager.ACCOUNTMANAGER.addOneTimePassword(email, password);
                    DemoApplication.emailManager.sendNewPassword(email, password);

                    return "redirect";
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        model.addAttribute("accountExistsError", true);
        return "ForgotPassword";
    }

    @RequestMapping({"/ForgotPassword", "/ForgotPassword.html"})
    public String getPage() {
        return "ForgotPassword";
    }

}
