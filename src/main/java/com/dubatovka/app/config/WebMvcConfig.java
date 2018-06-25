package com.dubatovka.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesViewResolver;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    /* ********* Defining a layout with Apache Tiles views ********* */
    @Bean
    public TilesConfigurer tilesConfigurer() {
        TilesConfigurer tiles = new TilesConfigurer();
        tiles.setDefinitions("/layout/tiles.xml");
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
    /* **************************/
    
    
}
