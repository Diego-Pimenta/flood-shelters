package com.compass.dao.impl;

import com.compass.dao.OrderDao;
import com.compass.exception.DbException;
import com.compass.model.entities.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;

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
}
