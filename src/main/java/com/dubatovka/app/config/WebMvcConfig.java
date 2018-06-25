package com.dubatovka.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesViewResolver;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    /* ********* Configure View Resolver***********
     * The ViewResolver maps view names to actual views.
     * Equivalents of View resolver:
     * InternalResourceViewResolver - for JSP
     * TilesViewResolver - Apache Tile
     * FreeMarkerViewResolver - views as FreeMarker templates
     * VelocityViewResolver - views as Velocity templates */
    
    /* ********* Configuring a JSP view resolver **********/
    @Bean
    public InternalResourceViewResolver internalResourceViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/pages/");
        viewResolver.setSuffix(".jsp");
        // this will ensure that JSTL’s formatting and message tags will get the Locale and message sources configured in Spring
        viewResolver.setExposeContextBeansAsAttributes(true); // added by myself
        return viewResolver;
    }
    
    /* ********* Configuring a Thymeleaf view resolver **********/
    @Bean
    public ThymeleafViewResolver thymeleafViewResolver(SpringTemplateEngine templateEngine) {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine);
        viewResolver.setOrder(1);
        
        /*Thymeleaf throws an error when trying to find pages outside of their view resolver instead of passing it onto the next view resolver. By setting the excludeViewNames, skips trying to resolve the view name within Thymeleaf.*/
        String[] excludedViews = new String[]{"index", "main", "xxx",};
        viewResolver.setExcludedViewNames(excludedViews);
        
        return viewResolver;
    }
    
    /* ********* Configuring a layout with Apache Tiles views ********* */
    @Bean
    public TilesConfigurer tilesConfigurer() {
        TilesConfigurer tiles = new TilesConfigurer();
        tiles.setDefinitions("/WEB-INF/layout/tiles.xml");
        tiles.setCheckRefresh(true);
        return tiles;
    }
    
    @Bean
    public TilesViewResolver tilesViewResolver() {
        TilesViewResolver tilesViewResolver = new TilesViewResolver();
        /*we’ll need to define an order for these resolvers. The order property is used to define which is the order of invocations in the chain. The higher the order property (largest order number), the later the view resolver is positioned in the chain.
         * Be careful on the order priority as the InternalResourceViewResolver should have a higher order – because it’s intended to represent a very explicit mapping. And if other resolvers have a higher order, then the InternalResourceViewResolver might never be invoked.*/
        tilesViewResolver.setOrder(0);
        return tilesViewResolver;
    }
}
