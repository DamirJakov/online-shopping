package pl.codementors.finalproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import pl.codementors.finalproject.FinalprojectApplication;
import pl.codementors.finalproject.model.LoginUserInfo;
import pl.codementors.finalproject.repository.ProductRepository;

import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class GlobalController {
    private static final Logger LOGGER = Logger.getLogger(FinalprojectApplication.class.getName());

    @Autowired
    private ProductRepository productRepository;
    @GetMapping("/")
    public String showHome(Model model, @CookieValue(value = "lang", defaultValue = "en") String lang) {
        final String id = LoginUserInfo.getUserID();
        LOGGER.log(Level.INFO, "Home User id:" + id);
        model.addAttribute("products", productRepository.findAllByAvailable(true));
        model.addAttribute("currentUser", LoginUserInfo.getUser());
        model.addAttribute("lang", lang);
        if ("".equals(id)) {
            model.addAttribute("login", "0");
        } else {
            model.addAttribute("login", id);
        }
        return "index";
    }
}
