package com.compass;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class Main {

    public static void main(String[] args) {
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU"); EntityManager em = emf.createEntityManager()) {
            System.out.println("Hello World");
        } catch (Exception e) {
            System.err.println("Ops");
        }
    }
}