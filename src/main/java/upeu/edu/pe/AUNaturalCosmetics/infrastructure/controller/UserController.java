/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package upeu.edu.pe.AUNaturalCosmetics.infrastructure.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import upeu.edu.pe.AUNaturalCosmetics.app.service.UserService;

/**
 *
 * @author alejandromacedop
 */
@Controller
@RequestMapping("/users")
public class UserController {
    
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping
    public String user(Model model){
        model.addAttribute("usuarios", userService.getUsers());
        return "/admin/usuarios";
        
    }
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Integer id){
        userService.deleteUserById(id);
        return "redirect:/";
    }
}
