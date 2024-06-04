package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Query("SELECT p FROM Payment p WHERE p.group.id = :groupId and p.archived = false")
    List<Payment> findAllByGroupIdNotArchived(@Param("groupId") Long groupId);

    @Query("SELECT p FROM Payment p WHERE p.group.id = :groupId")
    List<Payment> findAllByGroupId(@Param("groupId") Long groupId);
}
