package com.compass.dao.impl;

import com.compass.dao.DonationDao;
import com.compass.dao.exception.DbException;
import com.compass.model.entities.Donation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class DonationDaoImpl implements DonationDao {

    private final EntityManager em;

    @Override
    public void save(Donation donation) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(donation);
            transaction.commit();
        } catch (PersistenceException e) {
            if (transaction.isActive()) transaction.rollback();
            throw new DbException("Error saving donation");
        }
    }

    @Override
    public List<Donation> findAll() {
        try {
            return em.createQuery("SELECT d FROM Donation d", Donation.class).getResultList();
        } catch (PersistenceException e) {
            throw new DbException("Error finding all donations");
        }
    }

    @Override
    public Donation findById(Long id) {
        try {
            return em.find(Donation.class, id);
        } catch (PersistenceException e) {
            throw new DbException("Error finding donation");
        }
    }

    @Override
    public void update(Donation donation) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(donation);
            transaction.commit();
        } catch (PersistenceException e) {
            if (transaction.isActive()) transaction.rollback();
            throw new DbException("Error updating donation");
        }
    }

    @Override
    public void delete(Long id) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Donation donation = em.find(Donation.class, id);
            if (donation != null) em.remove(donation);
            transaction.commit();
        } catch (PersistenceException e) {
            if (transaction.isActive()) transaction.rollback();
            throw new DbException("Error deleting donation");
        }
    }
}
