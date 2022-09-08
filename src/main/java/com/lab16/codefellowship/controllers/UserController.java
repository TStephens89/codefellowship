package com.lab16.codefellowship.controllers;

import com.lab16.codefellowship.models.ApplicationUser;
import com.lab16.codefellowship.repositories.ApplicationUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDateTime;

@Controller
public class UserController {
    // Autowire user repo
    @Autowired
    ApplicationUserRepo applicationUserRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    HttpServletRequest request;

    // GET ROUTES

    // Step 8: Make a home page
    @GetMapping("/")
    public String getHomePage(Principal p, Model m)
    {
        if (p != null)
        {
            String username = p.getName();
            ApplicationUser dinoUser = applicationUserRepo.findByUsername(username);

            m.addAttribute("username", username);
            m.addAttribute("firstName", dinoUser.getFirstName());
        }

        return "index.html";
    }
    @GetMapping("/users/{id}")
    public String getUserInfo(Model m, Principal p, @PathVariable Long id){
        if (p != null) { // not strictly required IF your WebSecurityConfig is correct
            String username = p.getName();
            ApplicationUser applicationUser = applicationUserRepo.findByUsername(username);

            m.addAttribute("username", username);
            m.addAttribute("firstName", applicationUser.getFirstName());
        }

        ApplicationUser dbUser = applicationUserRepo.findById(id).orElseThrow();
        m.addAttribute("dbUserUsername", dbUser.getUsername());
        m.addAttribute("dbUserFirstName", dbUser.getFirstName());
        m.addAttribute("dbUserId", dbUser.getId());

        m.addAttribute("testDate", LocalDateTime.now());

        return "user-info";
    }

    @GetMapping("/login")
    public String getLoginPage(){
        return "login";
    }

    @GetMapping("/signup")
    public String getSignupPage(){
        return "signup";
    }

//    @GetMapping("/sauce")
//    public String getSecretSauce(){
//        return "secretSauce";
//    }


    @PostMapping("/signup")
    public RedirectView createUser(String username, String firstName, String password, String lastName, String dateOfBirth, String bio){
//        String hashedPassword = passwordEncoder.encode(password);
        ApplicationUser newUser = new ApplicationUser(username, passwordEncoder.encode(password), firstName, lastName, dateOfBirth, bio);
        applicationUserRepo.save(newUser);
        // pre auth with HttpServletReq
        authWithHttpServletRequest(username, password);
        return new RedirectView("/");
    }
    @PutMapping("/users/{id}")
    public RedirectView editUserInfo(Model m, Principal p, @PathVariable Long id, String username, String firstName, RedirectAttributes redir){
        if(p != null && p.getName().equals(username)){
            ApplicationUser newUser = applicationUserRepo.findById(id).orElseThrow();
            newUser.setUsername(username);
            newUser.setFirstName(newUser.firstName);
            applicationUserRepo.save(newUser);
        } else {
            redir.addFlashAttribute("errorMessage", "Cannot edit another user's info");
        }
        return new RedirectView("/users/" + id);

    }

    public void authWithHttpServletRequest(String username, String password){
        try {
            request.login(username, password);
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }
}
