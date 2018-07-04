package com.dubatovka.app.service.impl;

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
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
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
@Configuration
@ComponentScan
public class ServiceFactory {
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public BetService getBetService() {
        return new BetServiceImpl();
    }
    
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public CategoryService getCategoryService() {
        return new CategoryServiceImpl();
    }
    
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public EventService getEventService() {
        EventServiceImpl eventService = new EventServiceImpl();
        eventService.setServiceFactory(this);
        return eventService;
    }
    
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public MessageService getMessageService() {
        return new MessageServiceImpl();
    }
    
    public MessageService getMessageService(String locale) {
        return new MessageServiceImpl(locale);
    }
    
    public MessageService getMessageService(Locale locale, String pathToBundle) {
        return new MessageServiceImpl(locale, pathToBundle);
    }
    
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public MessageService getMessageService(HttpSession session) {
        return new MessageServiceImpl(session);
    }
    
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public OutcomeService getOutcomeService() {
        return new OutcomeServiceImpl();
    }
    
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public PaginationService getPaginationService() {
        return new PaginationServiceImpl();
    }
    
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public PlayerService getPlayerService() {
        return new PlayerServiceImpl();
    }
    
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public TransactionService getTransactionService() {
        return new TransactionServiceImpl();
    }
    
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public UserService getUserService() {
        return new UserServiceImpl();
    }
    
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ValidationService getValidationService() {
        return new ValidationServiceImpl();
    }
}