package com.compass.model.entities;

import com.compass.model.entities.enums.ItemType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
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

    // função para ser utilizada no serviço a fim de impedir que um centro de dist. tenha mais que 1000 itens de um mesmo tipo
    public Map<ItemType, Integer> getTotalItemsByType() {
        Map<ItemType, Integer> totalItemsByType = new HashMap<>();

        for (Item item : items) {
            // faz um merge para adicionar uma chave ao mapa ou atualizar algum valor
            totalItemsByType.merge(item.getItemType(), item.getQuantity(), Integer::sum);
        }
        return totalItemsByType;
    }
}