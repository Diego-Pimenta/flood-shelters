package com.compass.dao.impl;

import com.compass.dao.OrderDao;
import com.compass.exception.DbException;
import com.compass.model.entities.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;

import java.util.List;

public class OrderDaoImpl implements OrderDao {

    private final EntityManager em;

    public OrderDaoImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(Order order) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(order);
            transaction.commit();
        } catch (PersistenceException e) {
            if (transaction.isActive()) transaction.rollback();
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<Order> findAll() {
        try {
            return em.createQuery("SELECT o FROM Order o", Order.class).getResultList();
        } catch (PersistenceException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public Order findById(Long id) {
        try {
            return em.find(Order.class, id);
        } catch (PersistenceException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<Order> findByShelterId(Long shelterId) {
        try {
            return em.createQuery("SELECT o FROM Order o WHERE o.shelter.id = :shelterId", Order.class)
                    .setParameter("shelterId", shelterId)
                    .getResultList();
        } catch (PersistenceException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public void update(Order order) {
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.merge(order);
            transaction.commit();
        } catch (PersistenceException e) {
            if (transaction.isActive()) transaction.rollback();
            throw new DbException(e.getMessage());
        }
    }
}
