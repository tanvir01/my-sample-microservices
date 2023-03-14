package com.tanservices.shipment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = "shipments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value = {"id","status"}, allowGetters=true)
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shipment_seq")
    @SequenceGenerator(name = "shipment_seq", sequenceName = "shipment_seq", allocationSize = 1)
    @JsonProperty("id")
    private Long id;

    @NotNull
    @Column(nullable = false, unique=true)
    @JsonProperty("orderId")
    private Long orderId;

    @NotNull
    @Column(nullable = false)
    @JsonProperty("address")
    private String address;

    @NotNull
    @Column(nullable = false, unique=true)
    @Size(min = 10, max = 30)
    @JsonProperty("trackingCode")
    private String trackingCode;

    @NotNull
    @Column(nullable = false)
    @ColumnDefault("'NEW'")
    @Enumerated(EnumType.STRING)
    @JsonProperty("status")
    private ShipmentStatus status;

    public enum ShipmentStatus {
        NEW,
        COMPLETED
    }
}
