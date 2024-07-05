package com.compass.model.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Name must not be blank")
    @Size(max = 255, message = "Name must be less than or equal to 255 characters")
    private String name;

    @NotBlank(message = "Address must not be blank")
    @Size(max = 255, message = "Address must be less than or equal to 255 characters")
    private String address;

    @NotBlank(message = "Responsible person must not be blank")
    @Size(max = 255, message = "Responsible person must be less than or equal to 255 characters")
    private String responsible;

    @NotBlank(message = "Phone number must not be blank")
    @Pattern(regexp = "^\\+55\\(\\d{2}\\)\\d{4,5}-\\d{4}$", message = "Invalid brazilian phone number")
    @Column(name = "phone_number")
    private String phoneNumber;

    @NotBlank(message = "Email must not be blank")
    @Size(max = 255, message = "Email must be less than or equal to 255 characters")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Capacity must not be null")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @NotNull(message = "Occupation must not be null")
    @Min(value = 0, message = "Occupation must be at least 0")
    private Integer occupation;

    @OneToMany(mappedBy = "shelter", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();

    public double getOccupationPercentage() {
        int totalItems = orders
                .stream()
                .mapToInt(order -> order.getItems()
                        .stream()
                        .mapToInt(Item::getQuantity)
                        .sum())
                .sum();
        return (double) (totalItems / capacity) * 100;
    }

    public void setOccupationPercentage(double percentage) {
        this.occupation = (int) ((percentage * capacity) / 100);
    }
}
