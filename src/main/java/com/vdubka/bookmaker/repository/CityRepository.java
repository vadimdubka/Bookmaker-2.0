package com.vdubka.bookmaker.repository;

import com.vdubka.bookmaker.bean.City;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends CrudRepository<City, Long> {

}