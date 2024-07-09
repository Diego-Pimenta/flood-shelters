package com.compass.model.entities;

import com.compass.model.enums.ClothingGenre;
import com.compass.model.enums.ClothingSize;
import com.compass.model.enums.ItemType;
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

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "tb_item")
public class Item implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

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

    public Item() {
    }

    public Item(Long id, String name, ItemType itemType, String description, ClothingGenre genre, ClothingSize size, Integer quantity, String measuringUnit, LocalDate validity, Donation donation, Order order) {
        this.id = id;
        this.name = name;
        this.itemType = itemType;
        this.description = description;
        this.genre = genre;
        this.size = size;
        this.quantity = quantity;
        this.measuringUnit = measuringUnit;
        this.validity = validity;
        this.donation = donation;
        this.order = order;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ClothingGenre getGenre() {
        return genre;
    }

    public void setGenre(ClothingGenre genre) {
        this.genre = genre;
    }

    public ClothingSize getSize() {
        return size;
    }

    public void setSize(ClothingSize size) {
        this.size = size;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getMeasuringUnit() {
        return measuringUnit;
    }

    public void setMeasuringUnit(String measuringUnit) {
        this.measuringUnit = measuringUnit;
    }

    public LocalDate getValidity() {
        return validity;
    }

    public void setValidity(LocalDate validity) {
        this.validity = validity;
    }

    public Donation getDonation() {
        return donation;
    }

    public void setDonation(Donation donation) {
        this.donation = donation;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", itemType=" + itemType +
                ", description='" + description + '\'' +
                ", genre=" + genre +
                ", size=" + size +
                ", quantity=" + quantity +
                ", measuringUnit='" + measuringUnit + '\'' +
                ", validity=" + validity +
                '}';
    }
}
