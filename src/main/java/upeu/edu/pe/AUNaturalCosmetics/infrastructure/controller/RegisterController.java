/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package upeu.edu.pe.AUNaturalCosmetics.infrastructure.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import upeu.edu.pe.AUNaturalCosmetics.app.service.EmailService;
import upeu.edu.pe.AUNaturalCosmetics.app.service.RegistrationService;
import upeu.edu.pe.AUNaturalCosmetics.infrastructure.dto.UserDto;

/**
 *
 * @author alejandromacedop
 */
@Controller
@RequestMapping("/register")
public class RegisterController {
    
    private final EmailService emailService;
    private final RegistrationService registrationService;      
    private final Logger log = LoggerFactory.getLogger(RegisterController.class);

    public RegisterController(EmailService emailService, RegistrationService registrationService) {
        this.emailService = emailService;
        this.registrationService = registrationService;
    }

    

    @GetMapping
    public String register(UserDto userDto){
        return "register";
    }
    
     @PostMapping
    public String registerUser(@Valid UserDto userDto, BindingResult bindingResult, RedirectAttributes redirectAttributes){
//        user.setDateCreated(LocalDateTime.now());
//        user.setUserType(UserType.USER);
//        user.setUsername(user.getEmail());
        String to = userDto.getEmail();
        String subject = "Correo Creado Exitosamente";
        log.info("correo enviado: {}", userDto);
        String message = "¡Tu cuenta de usuario ha sido creada exitosamente! Gracias por registrarte en nuestra tienda E-commerce, disfurte de nuestros productos";
        emailService.sendEmail(to, subject, message);
        log.info("correo enviado: {}", message);

        if(bindingResult.hasErrors()){
            bindingResult.getAllErrors().forEach(
                    e->{ log.info( "Error {}", e.getDefaultMessage() ); }
            );
            return "register";
        }
        registrationService.register(userDto.userDtoToUser());
        redirectAttributes.addFlashAttribute("success", "Usuario creado correctamente");
        return "redirect:/login";
    }
    
    
    
}
