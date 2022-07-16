package com.obm.ukrainiansmessenger.controller;

import com.obm.ukrainiansmessenger.models.User;
import com.obm.ukrainiansmessenger.models.UserTransporter;
import com.obm.ukrainiansmessenger.servise.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SecurityController {

    private final UserService userService;

    public SecurityController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String reg() {
        return "login";
    }

    @PostMapping("/")
    public String addUser(@RequestParam("email") String email, Model model) {
        if (userService.searchEmailInDB(email)) {
            User user = new User();
            user.setEmail(email);
            userService.addUser(user);
        }
        return "redirect:/activate";
    }

    @GetMapping("/activate")
    public String activate() {
        return "activate";
    }

    @PostMapping("/activate")
    public String sendActivateCode(@RequestParam("code") String code, Model model) {
        User user = userService.activeUser(code);
        if (user.getActivationCode() == null) {
            UserTransporter.setUser(user);
            if (user.getUsername()==null && user.getPassword()==null) {
                return "redirect:/page/registration";
            }else {
                return "redirect:/page/login";
            }
        }
        return "activate";
    }

    @GetMapping("/page/registration")
    public String pageRegistration(){
        return "page-registration";
    }

    @GetMapping("/page/login")
    public String pageLogin(Model model){
        User user = UserTransporter.getUser();
        System.out.println(user.getUsername());
        model.addAttribute("user", user);
        return "page-login";
    }

    @PostMapping("/page/registration")
    public String pageRegistration(@RequestParam("username")String username,
                                   @RequestParam("password")String password){
        User user = UserTransporter.getUser();
        if(userService.save(username,password,user)){
            return "redirect:/page/login";
        }
        return "page-registration";
    }
}