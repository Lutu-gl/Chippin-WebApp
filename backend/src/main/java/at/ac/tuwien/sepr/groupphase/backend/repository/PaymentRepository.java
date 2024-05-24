package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE Payment p SET p.deleted = true WHERE p = :payment")
    void markPaymentAsDeleted(Payment payment);

    @Transactional
    @Modifying
    @Query("UPDATE Payment p SET p.deleted = false WHERE p = :payment")
    void markPaymentAsRecovered(Payment payment);
}
