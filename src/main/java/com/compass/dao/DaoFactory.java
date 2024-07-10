package com.compass.dao;

import com.compass.dao.impl.DistributionCenterDaoImpl;
import com.compass.dao.impl.DonationDaoImpl;
import com.compass.dao.impl.OrderDaoImpl;
import com.compass.dao.impl.ShelterDaoImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DaoFactory {

    private static EntityManagerFactory emf;
    private static EntityManager em;

    public static void initDbOperations() {
        emf = Persistence.createEntityManagerFactory("myPU");
        em = emf.createEntityManager();
    }

    public static DonationDao createDonationDao() {
        return new DonationDaoImpl(em);
    }

    public static ShelterDao createShelterDao() {
        return new ShelterDaoImpl(em);
    }

    public static DistributionCenterDao createDistributionCenterDao() {
        return new DistributionCenterDaoImpl(em);
    }

    public static OrderDao createOrderDao() {
        return new OrderDaoImpl(em);
    }

    public static void closeAll() {
        if (em.isOpen()) em.close();
        if (emf.isOpen()) emf.close();
    }
}
