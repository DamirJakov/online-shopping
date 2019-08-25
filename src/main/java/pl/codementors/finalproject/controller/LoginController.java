package pl.codementors.finalproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.codementors.finalproject.FinalprojectApplication;
import pl.codementors.finalproject.model.LoginUserInfo;

import javax.servlet.http.HttpServletResponse;
import java.util.logging.*;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;


@Controller
public class LoginController {
    private static final Logger LOGGER = Logger.getLogger(FinalprojectApplication.class.getName());

    @GetMapping(value = "/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model,
                        HttpServletResponse respond) {


        LOGGER.log(Level.INFO, "User id:" + LoginUserInfo.getUserID());

        if (error != null) {
            model.addAttribute("error", error);
            respond.setStatus(SC_BAD_REQUEST);
        }else if (logout != null) {
            model.addAttribute("logout", logout);
            respond.setStatus(SC_OK);
        }else if (!"".equals(LoginUserInfo.getUserID())){
            model.addAttribute("login", true);
        }
        return "login";
    }

//    @PostMapping("/loginpass")
//    public String showPassword(@RequestParam("pass") String pass) {
//        final BCryptPasswordEncoder cryptPasswordEncoder = new BCryptPasswordEncoder();
//        System.out.println(cryptPasswordEncoder.encode(pass));
//        return "login";
//    }
}
