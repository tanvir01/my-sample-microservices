package com.tanservices.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    @SequenceGenerator(
            name = "order_id_sequence",
            sequenceName = "order_id_sequence",
            allocationSize=1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "order_id_sequence"
    )
    @JsonProperty("id")
    private Long id;

    @NotNull
    @Column(nullable = false)
    @JsonProperty("customer_name")
    private String customerName;

    @NotNull
    @Column(nullable = false)
    @JsonProperty("customer_email")
    private String customerEmail;

    @NotNull
    @Column(nullable = false)
    @JsonProperty("shipping_address")
    private String shippingAddress;

    @NotNull
    @Column(nullable = false)
    @JsonProperty("total_amount")
    private Double totalAmount;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @JsonProperty("status")
    private OrderStatus status;

    public enum OrderStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        CANCELLED
    }

}

