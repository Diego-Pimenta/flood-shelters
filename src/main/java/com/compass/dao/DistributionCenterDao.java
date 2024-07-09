package com.compass.dao;

import com.compass.model.entities.DistributionCenter;

import java.util.List;

public interface DistributionCenterDao {

    DistributionCenter findById(Long id);
    List<DistributionCenter> findAll();
}
