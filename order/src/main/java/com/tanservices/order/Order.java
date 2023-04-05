package com.tanservices.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.jetbrains.annotations.NotNull;


@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value = {"id"}, allowGetters=true)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
    @SequenceGenerator(name = "order_seq", sequenceName = "order_seq", allocationSize = 1)
    @JsonProperty("id")
    private Long id;

    @NotNull
    @Column(nullable = false)
    @JsonProperty("customerName")
    private String customerName;

    @NotNull
    @Column(nullable = false)
    @JsonProperty("customerEmail")
    private String customerEmail;

    @NotNull
    @Column(nullable = false)
    @JsonProperty("totalAmount")
    private Double totalAmount;

    @NotNull
    @Column(nullable = false)
    @ColumnDefault("'PENDING'")
    @Enumerated(EnumType.STRING)
    @JsonProperty("status")
    private OrderStatus status;

}

