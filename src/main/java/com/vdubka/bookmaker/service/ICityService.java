package com.vdubka.bookmaker.service;

import com.vdubka.bookmaker.bean.City;

import java.util.List;

public interface ICityService {
    
    public List<City> findAll();
}