package com.compass.dao;

import com.compass.model.entities.Shelter;

import java.util.List;

public interface ShelterDao {

    void save(Shelter shelter);
    List<Shelter> findAll();
    Shelter findById(Long id);
    void update(Shelter shelter);
    void delete(Long id);
}
