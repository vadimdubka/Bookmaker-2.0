package com.dubatovka.app.service.impl;

import com.dubatovka.app.entity.City;
import com.dubatovka.app.repository.CityRepository;
import com.dubatovka.app.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityServiceImpl implements CityService {
    
    @Autowired
    private CityRepository repository;
    
    @Override
    public List<City> findAll() {
        
        List<City> cities = (List<City>) repository.findAll();
        
        return cities;
    }
}
