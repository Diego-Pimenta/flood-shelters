package com.compass.dao;

import com.compass.model.entities.Donation;

import java.util.List;

public interface DonationDao {

    void save(Donation donation);
    List<Donation> findAll();
    Donation findById(Long id);
    void update(Donation donation);
    void delete(Long id);
}
