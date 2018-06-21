package com.dubatovka.app.service.impl;

import com.dubatovka.app.dao.impl.DAOProvider;
import com.dubatovka.app.service.BetService;
import com.dubatovka.app.service.CategoryService;
import com.dubatovka.app.service.EventService;
import com.dubatovka.app.service.MessageService;
import com.dubatovka.app.service.OutcomeService;
import com.dubatovka.app.service.PaginationService;
import com.dubatovka.app.service.PlayerService;
import com.dubatovka.app.service.TransactionService;
import com.dubatovka.app.service.UserService;
import com.dubatovka.app.service.ValidationService;

import javax.servlet.http.HttpSession;
import java.util.Locale;

/**
 * The class provides access to Service layer classes implementations.
 *
 * @author Dubatovka Vadim
 */
public final class ServiceFactory {
    /**
     * Private constructor to forbid create {@link ServiceFactory} instances.
     */
    private ServiceFactory() {
    }
    
    public static BetService getBetService() {
        return new BetServiceImpl();
    }
    
    public static BetService getBetService(DAOProvider daoProvider) {
        return new BetServiceImpl(daoProvider);
    }
    
    public static CategoryService getCategoryService() {
        return new CategoryServiceImpl();
    }
    
    public static CategoryService getCategoryService(DAOProvider daoProvider) {
        return new CategoryServiceImpl(daoProvider);
    }
    
    public static EventService getEventService() {
        return new EventServiceImpl();
    }
    
    public static EventService getEventService(DAOProvider daoProvider) {
        return new EventServiceImpl(daoProvider);
    }
    
    public static MessageService getMessageService() {
        return new MessageServiceImpl();
    }
    
    public static MessageService getMessageService(String locale) {
        return new MessageServiceImpl(locale);
    }
    
    public static MessageService getMessageService(Locale locale, String pathToBundle) {
        return new MessageServiceImpl(locale, pathToBundle);
    }
    
    public static MessageService getMessageService(HttpSession session) {
        return new MessageServiceImpl(session);
    }
    
    public static OutcomeService getOutcomeService() {
        return new OutcomeServiceImpl();
    }
    
    public static OutcomeService getOutcomeService(DAOProvider daoProvider) {
        return new OutcomeServiceImpl(daoProvider);
    }
    
    public static PaginationService getPaginationService() {
        return new PaginationServiceImpl();
    }
    
    public static PlayerService getPlayerService() {
        return new PlayerServiceImpl();
    }
    
    public static PlayerService getPlayerService(DAOProvider daoProvider) {
        return new PlayerServiceImpl(daoProvider);
    }
    
    public static TransactionService getTransactionService() {
        return new TransactionServiceImpl();
    }
    
    public static TransactionService getTransactionService(DAOProvider daoProvider) {
        return new TransactionServiceImpl(daoProvider);
    }
    
    public static UserService getUserService() {
        return new UserServiceImpl();
    }
    
    public static UserService getUserService(DAOProvider daoProvider) {
        return new UserServiceImpl(daoProvider);
    }
    
    public static ValidationService getValidationService() {
        return new ValidationServiceImpl();
    }
}