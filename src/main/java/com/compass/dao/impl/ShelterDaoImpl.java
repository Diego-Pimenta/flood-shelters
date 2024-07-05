package com.compass.dao.impl;

import com.compass.dao.ShelterDao;
import com.compass.dao.exception.DbException;
import com.compass.model.entities.Shelter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ShelterDaoImpl implements ShelterDao {

    private final EntityManager em;

    @Override
    public void save(Shelter shelter) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(shelter);
            transaction.commit();
        } catch (PersistenceException e) {
            if (transaction.isActive()) transaction.rollback();
            throw new DbException("Error saving shelter");
        }
    }

    @Override
    public List<Shelter> findAll() {
        try {
            return em.createQuery("SELECT s FROM Shelter s", Shelter.class).getResultList();
        } catch (PersistenceException e) {
            throw new DbException("Error finding all shelters");
        }
    }

    @Override
    public Shelter findById(Long id) {
        try {
            return em.find(Shelter.class, id);
        } catch (PersistenceException e) {
            throw new DbException("Error finding shelter");
        }
    }

    @Override
    public void update(Shelter shelter) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(shelter);
            transaction.commit();
        } catch (PersistenceException e) {
            if (transaction.isActive()) transaction.rollback();
            throw new DbException("Error updating shelter");
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
        } catch (PersistenceException e) {
            if (transaction.isActive()) transaction.rollback();
            throw new DbException("Error deleting shelter");
        }
    }
}
