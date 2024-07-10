package com.compass.dao;

import com.compass.model.entities.Donation;

import java.util.List;

public interface DonationDao {

    void save(Donation donation);
    List<Donation> findAll();
    Donation findById(Long id);
    List<Donation> findByItemName(String itemName);
    List<Donation> findByDistributionCenterId(Long distributionCenterId);
    List<Donation> findByItemNameAndDistributionCenterId(String itemName, Long distributionCenterId);
    void update(Donation donation);
    void delete(Long id);
}
