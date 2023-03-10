package com.example.money_way.model;

import com.example.money_way.enums.Status;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "transaction_tbl")
public class Transaction {

    @Id
    @Column(nullable = false)
    private Long transactionId;

    @Column(nullable = false)
    private String currency;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(nullable = false)
    private String virtualAccountRef;
    private String description;
    @Enumerated(EnumType.STRING)
    private Status status;
    private String request_id;
    private String responseMessage;
    private String providerStatus;
    private String paymentType;
    @Column(nullable = false)
    private Long userId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createdAt")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="last_updated")
    private Date updatedAt;


    @PrePersist
    public void createdAt(){

        this.createdAt = new Date();
    }

    @PreUpdate
    public void updatedAt(){

        this.updatedAt = new Date();
    }


}
