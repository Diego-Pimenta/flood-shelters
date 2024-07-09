package com.compass.dao.impl;

import com.compass.dao.DistributionCenterDao;
import com.compass.exception.DbException;
import com.compass.model.entities.DistributionCenter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;

import java.util.List;

public class DistributionCenterDaoImpl implements DistributionCenterDao {

    private final EntityManager em;

    public DistributionCenterDaoImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public DistributionCenter findById(Long id) {
        try {
            return em.find(DistributionCenter.class, id);
        } catch (PersistenceException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<DistributionCenter> findAll() {
        try {
            return em.createQuery("SELECT dc FROM DistributionCenter dc", DistributionCenter.class).getResultList();
        } catch (PersistenceException e) {
            throw new DbException(e.getMessage());
        }
    }
}
