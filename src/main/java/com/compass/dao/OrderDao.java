package com.compass.dao;

import com.compass.model.entities.Order;

import java.util.List;

public interface OrderDao {

    void save(Order order);
    List<Order> findByShelterId(Long shelterId);
}
