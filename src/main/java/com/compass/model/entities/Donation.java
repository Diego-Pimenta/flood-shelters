package com.compass.model.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tb_donation")
public class Donation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "distribution_center_id")
    private DistributionCenter distributionCenter;

    @OneToMany(mappedBy = "donation", cascade = CascadeType.ALL)
    private Set<Item> items = new HashSet<>();

    public Donation() {
    }

    public Donation(Long id, DistributionCenter distributionCenter, Set<Item> items) {
        this.id = id;
        this.distributionCenter = distributionCenter;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DistributionCenter getDistributionCenter() {
        return distributionCenter;
    }

    public void setDistributionCenter(DistributionCenter distributionCenter) {
        this.distributionCenter = distributionCenter;
    }

    public Set<Item> getItems() {
        return items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Donation donation = (Donation) o;
        return Objects.equals(id, donation.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Donation{" +
                "id=" + id +
                ", distributionCenter=" + distributionCenter +
                ", items=" + items +
                '}';
    }
}