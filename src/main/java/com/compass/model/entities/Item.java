package com.compass.model.entities;

import com.compass.model.entities.enums.ClothingGenre;
import com.compass.model.entities.enums.ClothingSize;
import com.compass.model.entities.enums.ItemType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@Entity
@Table(name = "tb_item")
public class Item implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type")
    private ItemType itemType;

    private String description;

    @Enumerated(EnumType.STRING)
    private ClothingGenre genre;

    @Enumerated(EnumType.STRING)
    private ClothingSize size;

    private Integer quantity;

    @Column(name = "measuring_unit")
    private String measuringUnit;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate validity;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "donation_id")
    private Donation donation;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
