package com.dubatovka.app.service;

import com.dubatovka.app.entity.City;

import java.util.List;

public interface CityService {
    
    List<City> findAll();
}