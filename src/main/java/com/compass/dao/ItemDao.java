package com.compass.dao;

import com.compass.model.entities.Item;
import com.compass.model.enums.ClothingGenre;
import com.compass.model.enums.ClothingSize;
import com.compass.model.enums.ItemType;

import java.time.LocalDate;
import java.util.List;

public interface ItemDao {

    List<Item> findAll();
    Item findByItemValues(String name, ItemType itemType, String description, ClothingGenre genre, ClothingSize size, String measuringUnit, LocalDate validity);
}
