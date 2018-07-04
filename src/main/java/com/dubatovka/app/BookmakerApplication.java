package com.dubatovka.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication // Shortcut to @Configuration, @EnableAutoConfiguration and @ComponentScan
@EnableJpaRepositories // Search and enable all interfaces with @Repository annotation
/*Detect all classes with WebFilter, WebListener and WebServlet annotations.*/
@ServletComponentScan(basePackages = {
    "com.dubatovka.app.controller",
    "com.dubatovka.app.filter",
    "com.dubatovka.app.listener"
})
public class BookmakerApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(BookmakerApplication.class, args);
    }
}
