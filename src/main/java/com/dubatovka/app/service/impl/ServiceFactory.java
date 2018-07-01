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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.Locale;

/**
 * The class provides access to Service layer classes implementations.
 *
 * @author Dubatovka Vadim
 */
@Component
public final class ServiceFactory {
    @Bean
    @Scope("prototype")
    public BetService getBetService() {
        return new BetServiceImpl();
    }
    
    @Bean
    @Scope("prototype")
    public BetService getBetService(DAOProvider daoProvider) {
        return new BetServiceImpl(daoProvider);
    }
    
    @Bean
    @Scope("prototype")
    public CategoryService getCategoryService() {
        return new CategoryServiceImpl();
    }
    
    @Bean
    @Scope("prototype")
    public CategoryService getCategoryService(DAOProvider daoProvider) {
        return new CategoryServiceImpl(daoProvider);
    }
    
    @Bean
    @Scope("prototype")
    public EventService getEventService() {
        return new EventServiceImpl();
    }
    
    @Bean
    @Scope("prototype")
    public EventService getEventService(DAOProvider daoProvider) {
        return new EventServiceImpl(daoProvider);
    }
    
    @Bean
    @Scope("prototype")
    public MessageService getMessageService() {
        return new MessageServiceImpl();
    }
    
    @Bean
    @Scope("prototype")
    public MessageService getMessageService(String locale) {
        return new MessageServiceImpl(locale);
    }
    
    @Bean
    @Scope("prototype")
    public MessageService getMessageService(Locale locale, String pathToBundle) {
        return new MessageServiceImpl(locale, pathToBundle);
    }
    
    @Bean
    @Scope("prototype")
    public MessageService getMessageService(HttpSession session) {
        return new MessageServiceImpl(session);
    }
    
    @Bean
    @Scope("prototype")
    public OutcomeService getOutcomeService() {
        return new OutcomeServiceImpl();
    }
    
    @Bean
    @Scope("prototype")
    public OutcomeService getOutcomeService(DAOProvider daoProvider) {
        return new OutcomeServiceImpl(daoProvider);
    }
    
    @Bean
    @Scope("prototype")
    public PaginationService getPaginationService() {
        return new PaginationServiceImpl();
    }
    
    @Bean
    @Scope("prototype")
    public PlayerService getPlayerService() {
        return new PlayerServiceImpl();
    }
    
    @Bean
    @Scope("prototype")
    public PlayerService getPlayerService(DAOProvider daoProvider) {
        return new PlayerServiceImpl(daoProvider);
    }
    
    @Bean
    @Scope("prototype")
    public TransactionService getTransactionService() {
        return new TransactionServiceImpl();
    }
    
    @Bean
    @Scope("prototype")
    public TransactionService getTransactionService(DAOProvider daoProvider) {
        return new TransactionServiceImpl(daoProvider);
    }
    
    @Bean
    @Scope("prototype")
    public UserService getUserService() {
        return new UserServiceImpl();
    }
    
    @Bean
    @Scope("prototype")
    public UserService getUserService(DAOProvider daoProvider) {
        return new UserServiceImpl(daoProvider);
    }
    
    @Bean
    @Scope("prototype")
    public ValidationService getValidationService() {
        return new ValidationServiceImpl();
    }
}