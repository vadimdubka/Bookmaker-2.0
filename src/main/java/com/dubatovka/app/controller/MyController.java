package com.dubatovka.app.controller;

import com.dubatovka.app.entity.City;
import com.dubatovka.app.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class MyController {
    
    @Autowired
    CityService cityService;
    
    @RequestMapping("/showCities")
    public String findCities(Model model) {
        
        List<City> cities = (List<City>) cityService.findAll();
        
        model.addAttribute("cities", cities);
        
        return "showCities";
    }
}
