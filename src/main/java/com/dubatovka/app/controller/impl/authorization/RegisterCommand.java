package com.dubatovka.app.controller.impl.authorization;

import com.dubatovka.app.controller.Command;
import com.dubatovka.app.controller.PageNavigator;
import com.dubatovka.app.service.MessageService;
import com.dubatovka.app.service.PlayerService;
import com.dubatovka.app.service.ValidationService;
import com.dubatovka.app.service.impl.ServiceFactory;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.dubatovka.app.config.ConfigConstant.ATTR_BIRTHDATE_INPUT;
import static com.dubatovka.app.config.ConfigConstant.ATTR_EMAIL_INPUT;
import static com.dubatovka.app.config.ConfigConstant.ATTR_FNAME_INPUT;
import static com.dubatovka.app.config.ConfigConstant.ATTR_LNAME_INPUT;
import static com.dubatovka.app.config.ConfigConstant.ATTR_MNAME_INPUT;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_INVALID_BIRTHDATE;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_INVALID_EMAIL;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_INVALID_NAME;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_INVALID_PASSWORD;
import static com.dubatovka.app.config.ConfigConstant.MESSAGE_ERR_PASSWORD_MISMATCH;
import static com.dubatovka.app.config.ConfigConstant.PARAM_BIRTHDATE;
import static com.dubatovka.app.config.ConfigConstant.PARAM_EMAIL;
import static com.dubatovka.app.config.ConfigConstant.PARAM_FNAME;
import static com.dubatovka.app.config.ConfigConstant.PARAM_LNAME;
import static com.dubatovka.app.config.ConfigConstant.PARAM_MNAME;
import static com.dubatovka.app.config.ConfigConstant.PARAM_PASSWORD;
import static com.dubatovka.app.config.ConfigConstant.PARAM_PASSWORD_AGAIN;

/**
 * The class provides registration command implementation.
 *
 * @author Dubatovka Vadim
 */
public class RegisterCommand implements Command {
    /**
     * Method provides registration process for users.<p>Takes input parameters from {@link
     * HttpServletRequest#getParameter(String)} and validates them. If all the parameters are valid
     * converts them to relevant data types and passes converted parameters further to the Logic
     * layer. If process passed successfully navigates to {@link
     * PageNavigator#REDIRECT_GOTO_INDEX}, else navigates to{@link
     * PageNavigator#FORWARD_PAGE_REGISTER}</p>
     *
     * @param request {@link HttpServletRequest} from client
     * @return {@link PageNavigator}
     */
    @Override
    public PageNavigator execute(HttpServletRequest request) {
        HttpSession    session        = request.getSession();
        MessageService messageService = ServiceFactory.getMessageService(session);
        
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
        PageNavigator navigator = PageNavigator.FORWARD_PAGE_REGISTER;
        if (messageService.isErrMessEmpty()) {
            try (PlayerService playerService = ServiceFactory.getPlayerService()) {
                int regPlayerId = playerService.registerPlayer(email, password, fName,
                                                               mName, lName, birthDate);
                if (regPlayerId > 0) {
                    navigator = PageNavigator.REDIRECT_GOTO_INDEX;
                }
            }
        }
        setMessagesToRequest(messageService, request);
        return navigator;
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
    private static void validateCommand(String email, String password, String passwordAgain,
                                        String fName, String mName, String lName, String birthDate,
                                        MessageService messageService, ServletRequest request) {
        ValidationService validationService = ServiceFactory.getValidationService();
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
}