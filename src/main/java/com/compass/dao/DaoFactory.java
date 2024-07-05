package com.compass.dao;

import com.compass.dao.impl.DonationDaoImpl;
import com.compass.dao.impl.ShelterDaoImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class DaoFactory {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU");
    public static final EntityManager em = emf.createEntityManager();

    public static DonationDao createDonationDao() {
        return new DonationDaoImpl(em);
    }

    public static ShelterDao createShelterDao() {
        return new ShelterDaoImpl(em);
    }

    public static void closeAll() {
        if (em.isOpen()) em.close();
        if (emf.isOpen()) emf.close();
    }
}
