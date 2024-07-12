package com.compass.dao.impl;

import com.compass.dao.ItemDao;
import com.compass.exception.DbException;
import com.compass.model.entities.Item;
import com.compass.model.enums.ClothingGenre;
import com.compass.model.enums.ClothingSize;
import com.compass.model.enums.ItemType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;

import java.time.LocalDate;
import java.util.List;

public class ItemDaoImpl implements ItemDao {

    private final EntityManager em;

    public ItemDaoImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Item> findAll() {
        try {
            return em.createQuery("SELECT i FROM Item i", Item.class).getResultList();
        } catch (PersistenceException e) {
            throw new DbException(e.getMessage());
        }
    }

    public Item findByItemValues(String name, ItemType itemType, String description, ClothingGenre genre, ClothingSize size, String measuringUnit, LocalDate validity) {
        try {
            return em.createQuery("SELECT i FROM Item i " +
                                    "WHERE i.name = :name " +
                                    "AND i.itemType = :itemType " +
                                    "AND i.description = :description " +
                                    "AND i.genre = :genre " +
                                    "AND i.size = :size " +
                                    "AND i.measuringUnit = :measuringUnit " +
                                    "AND i.validity = :validity",
                            Item.class)
                    .setParameter("name", name)
                    .setParameter("itemType", itemType)
                    .setParameter("description", description)
                    .setParameter("genre", genre)
                    .setParameter("size", size)
                    .setParameter("measuringUnit", measuringUnit)
                    .setParameter("validity", validity)
                    .getResultStream().findFirst().orElse(null);
        } catch (PersistenceException e) {
            throw new DbException(e.getMessage());
        }
    }
}
