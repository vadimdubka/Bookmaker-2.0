package com.dubatovka.app.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {
    
    private ApplicationContext applicationContext;
    
    /*Spring will call setApplicationContext() and set current applicationcontext.*/
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    
    
    public ApplicationContext getContext() {
        return applicationContext;
    }
    
}