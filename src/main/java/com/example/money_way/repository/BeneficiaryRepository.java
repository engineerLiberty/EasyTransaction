package com.example.money_way.repository;

import com.example.money_way.model.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
    Optional<Beneficiary> findByAccountNumberAndUserId(String accountNumber, Long userId);
    Optional<Beneficiary>findBeneficiariesByPhoneNumberAndUserId(String phoneNumber, Long userId);
}