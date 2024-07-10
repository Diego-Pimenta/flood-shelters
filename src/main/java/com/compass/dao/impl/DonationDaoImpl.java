package com.compass.dao.impl;

import com.compass.dao.DonationDao;
import com.compass.exception.DbException;
import com.compass.exception.DbIntegrityException;
import com.compass.model.entities.Donation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;

import java.util.List;

public class DonationDaoImpl implements DonationDao {

    private final EntityManager em;

    public DonationDaoImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(Donation donation) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(donation);
            transaction.commit();
        } catch (PersistenceException e) {
            if (transaction.isActive()) transaction.rollback();
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<Donation> findAll() {
        try {
            return em.createQuery("SELECT d FROM Donation d", Donation.class).getResultList();
        } catch (PersistenceException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public Donation findById(Long id) {
        try {
            return em.find(Donation.class, id);
        } catch (PersistenceException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<Donation> findByItemName(String itemName) {
        try {
            return em.createQuery("SELECT d FROM Donation d WHERE LOWER(d.item.name) = LOWER(:itemName)", Donation.class)
                    .setParameter("itemName", itemName)
                    .getResultList();
        } catch (PersistenceException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<Donation> findByDistributionCenterId(Long distributionCenterId) {
        try {
            return em.createQuery("SELECT d FROM Donation d WHERE d.distributionCenter.id = :distributionCenterId", Donation.class)
                    .setParameter("distributionCenterId", distributionCenterId)
                    .getResultList();
        } catch (PersistenceException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<Donation> findByItemNameAndDistributionCenterId(String itemName, Long distributionCenterId) {
        try {
            return em.createQuery("SELECT d FROM Donation d WHERE LOWER(d.item.name) = LOWER(:itemName) AND d.distributionCenter.id = :distributionCenterId", Donation.class)
                    .setParameter("itemName", itemName)
                    .setParameter("distributionCenterId", distributionCenterId)
                    .getResultList();
        } catch (PersistenceException e) {
            throw new DbException(e.getMessage());
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
            throw new DbException(e.getMessage());
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
            throw new DbIntegrityException(e.getMessage());
        }
    }
}
