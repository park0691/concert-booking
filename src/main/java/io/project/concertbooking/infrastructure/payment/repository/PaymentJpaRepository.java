package io.project.concertbooking.infrastructure.payment.repository;

import io.project.concertbooking.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
}
