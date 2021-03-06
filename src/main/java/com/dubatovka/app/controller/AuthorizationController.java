package com.dubatovka.app.controller;

import com.dubatovka.app.entity.Admin;
import com.dubatovka.app.entity.Analyst;
import com.dubatovka.app.entity.Player;
import com.dubatovka.app.entity.User;
import com.dubatovka.app.service.MessageService;
import com.dubatovka.app.service.PlayerService;
import com.dubatovka.app.service.UserService;
import com.dubatovka.app.service.ValidationService;
import com.dubatovka.app.service.impl.ServiceFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.dubatovka.app.config.ConfigConstant.*;

@Controller
public class AuthorizationController extends AbstrController {
    public AuthorizationController(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }
    
    @PostMapping("/register")
    public String register(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        MessageService messageService = serviceFactory.getMessageService(session);
        
        String email = request.getParameter(PARAM_EMAIL);
        String password = request.getParameter(PARAM_PASSWORD);
        String passwordAgain = request.getParameter(PARAM_PASSWORD_AGAIN);
        String fName = request.getParameter(PARAM_FNAME);
        String mName = request.getParameter(PARAM_MNAME);
        String lName = request.getParameter(PARAM_LNAME);
        String birthDate = request.getParameter(PARAM_BIRTHDATE);
        
        validateRequestParams(messageService, email, password, passwordAgain,
                              fName, mName, lName, birthDate);
        validateCommand(email, password, passwordAgain,
                        fName, mName, lName, birthDate, messageService, request);
        String navigator = "forward:/register_page";
        if (messageService.isErrMessEmpty()) {
            try (PlayerService playerService = serviceFactory.getPlayerService()) {
                int regPlayerId = playerService.registerPlayer(email, password, fName,
                                                               mName, lName, birthDate);
                if (regPlayerId > 0) {
                    navigator = "redirect:/main_page";
                }
            }
        }
        setMessagesToRequest(messageService, request);
        return navigator;
    }
    
    @PostMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession();
        MessageService messageService = serviceFactory.getMessageService(session);
        
        String email = request.getParameter(PARAM_EMAIL);
        String password = request.getParameter(PARAM_PASSWORD);
        
        validateRequestParams(messageService, email, password);
        validateCommand(email, password, messageService);
        if (messageService.isErrMessEmpty()) {
            try (UserService userService = serviceFactory.getUserService()) {
                User user = userService.authorizeUser(email, password);
                if (user != null) {
                    setUserToSession(user, session);
                } else {
                    messageService.appendErrMessByKey(MESSAGE_ERR_LOGIN_MISMATCH);
                }
            }
        }
        setMessagesToRequest(messageService, request);
        return "redirect:/main_page";
    }
    
    @GetMapping("/logout")
    public String logout(Model model, HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/main_page";
    }
    
    
    /**
     * Method validates parameters using {@link ValidationService} to confirm that all necessary
     * parameters for command execution have proper state according to requirements for application.
     *
     * @param email          {@link String} parameter for validation
     * @param password       {@link String} parameter for validation
     * @param passwordAgain  {@link String} parameter for validation
     * @param fName          {@link String} parameter for validation
     * @param mName          {@link String} parameter for validation
     * @param lName          {@link String} parameter for validation
     * @param birthDate      {@link String} parameter for validation
     * @param messageService {@link MessageService} to hold message about result of validation
     * @param request        {@link ServletRequest} to add attributes with valid data
     */
    private void validateCommand(String email, String password, String passwordAgain,
                                 String fName, String mName, String lName, String birthDate,
                                 MessageService messageService, ServletRequest request) {
        ValidationService validationService = serviceFactory.getValidationService();
        if (validationService.isValidEmail(email)) {
            request.setAttribute(ATTR_EMAIL_INPUT, email);
        } else {
            messageService.appendErrMessByKey(MESSAGE_ERR_INVALID_EMAIL);
        }
        if (!validationService.isValidPassword(password, passwordAgain)) {
            messageService.appendErrMessByKey(MESSAGE_ERR_INVALID_PASSWORD);
            messageService.appendErrMessByKey(MESSAGE_ERR_PASSWORD_MISMATCH);
        }
        if (validationService.isValidName(fName)) {
            request.setAttribute(ATTR_FNAME_INPUT, fName);
        } else {
            messageService.appendErrMessByKey(MESSAGE_ERR_INVALID_NAME);
        }
        if (validationService.isValidName(mName)) {
            request.setAttribute(ATTR_MNAME_INPUT, mName);
        } else {
            messageService.appendErrMessByKey(MESSAGE_ERR_INVALID_NAME);
        }
        if (validationService.isValidName(lName)) {
            request.setAttribute(ATTR_LNAME_INPUT, lName);
        } else {
            messageService.appendErrMessByKey(MESSAGE_ERR_INVALID_NAME);
        }
        if (validationService.isValidBirthDate(birthDate)) {
            request.setAttribute(ATTR_BIRTHDATE_INPUT, birthDate);
        } else {
            messageService.appendErrMessByKey(MESSAGE_ERR_INVALID_BIRTHDATE);
        }
    }
    
    /**
     * Method validates parameters using {@link ValidationService} to confirm that all necessary
     * parameters for command execution have proper state according to requirements for application.
     *
     * @param email          {@link String} parameter for validation
     * @param password       {@link String} parameter for validation
     * @param messageService {@link MessageService} to hold message about validation result
     */
    private void validateCommand(String email, String password,
                                 MessageService messageService) {
        if (messageService.isErrMessEmpty()) {
            ValidationService validationService = serviceFactory.getValidationService();
            if (!validationService.isValidEmail(email)) {
                messageService.appendErrMessByKey(MESSAGE_ERR_INVALID_EMAIL);
            }
            if (!validationService.isValidPassword(password)) {
                messageService.appendErrMessByKey(MESSAGE_ERR_INVALID_PASSWORD);
            }
        }
    }
    
    /**
     * Method sets {@link User} to session.
     *
     * @param user    {@link User}
     * @param session {@link HttpSession}
     */
    private void setUserToSession(User user, HttpSession session) {
        session.setAttribute(ATTR_USER, user);
        session.setAttribute(ATTR_ROLE, user.getRole());
        Class userClass = user.getClass();
        if (userClass == Player.class) {
            Player player = (Player) user;
            try (PlayerService playerService = serviceFactory.getPlayerService()) {
                playerService.updatePlayerInfo(player);
            }
            session.setAttribute(ATTR_PLAYER, player);
        } else if (userClass == Admin.class) {
            Admin admin = (Admin) user;
            session.setAttribute(ATTR_ADMIN, admin);
        } else {
            Analyst analyst = (Analyst) user;
            session.setAttribute(ATTR_ANALYST, analyst);
        }
    }
}
