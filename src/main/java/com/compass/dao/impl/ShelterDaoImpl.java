package com.compass.dao.impl;

import com.compass.dao.ShelterDao;
import com.compass.exception.DbException;
import com.compass.exception.DbIntegrityException;
import com.compass.model.entities.Shelter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class ShelterDaoImpl implements ShelterDao {

    private final EntityManager em;

    public ShelterDaoImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(Shelter shelter) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(shelter);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<Shelter> findAll() {
        try {
            return em.createQuery("SELECT s FROM Shelter s", Shelter.class).getResultList();
        } catch (Exception e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public Shelter findById(Long id) {
        try {
            return em.find(Shelter.class, id);
        } catch (Exception e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public void update(Shelter shelter) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(shelter);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public void delete(Long id) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Shelter shelter = em.find(Shelter.class, id);
            if (shelter != null) em.remove(shelter);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            throw new DbIntegrityException(e.getMessage());
        }
    }
}
