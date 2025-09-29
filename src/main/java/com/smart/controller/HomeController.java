package com.smart.controller;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message; // ✅ Make sure this class exists

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Home-Smart Contact manager");
        return "home";
    }

    @RequestMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "About-Smart Contact manager");
        return "about";
    }

    @RequestMapping("/signup/")
    public String signup(Model model) {
        model.addAttribute("title", "Register-Smart Contact manager");
        model.addAttribute("user", new User());
        return "signup";
    }

    // this handler for registering user
    @RequestMapping(value = "/do_register", method = RequestMethod.POST)
    public String registerUser(@ModelAttribute("user") User user,
                               @RequestParam(value = "agreement", defaultValue = "false") boolean agreement,
                               Model model, HttpSession session) {

        try {
            if (!agreement) {
                System.out.println("You have not agreed to the terms and conditions");
                throw new Exception("You have not agreed to the terms and conditions");
            }

            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            System.out.println("Agreement: " + agreement);
            System.out.println("USER: " + user);

            User result = this.userRepository.save(user);
            model.addAttribute("user", new User());  // re-add empty form

            model.addAttribute("message", new Message("Successfully registered!!", "alert-success"));

            return "signup";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("user", user);
            model.addAttribute("message", new Message("Something went wrong: " + e.getMessage(), "alert-danger"));

            return "signup";
        }
        //handler for custom login
        
     
       
    
}
    @GetMapping("/sign")
    public String customLogin(Model model) {
    	model.addAttribute("title","Login page");
    	return "login";
    }
}
