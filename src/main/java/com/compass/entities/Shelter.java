package com.compass.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tb_shelter")
public class Shelter implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String responsible;

    @Column(name = "phone_number")
    private String phoneNumber;

    private String email;
    private Integer capacity;
    private Integer occupation;

    @OneToMany(mappedBy = "shelter", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();
}
