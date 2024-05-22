package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.payment.PaymentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.PaymentMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Activity;
import at.ac.tuwien.sepr.groupphase.backend.entity.ActivityCategory;
import at.ac.tuwien.sepr.groupphase.backend.entity.Payment;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ActivityRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PaymentRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.PaymentService;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.PaymentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentValidator paymentValidator;
    private final PaymentRepository paymentRepository;
    private final ActivityRepository activityRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public PaymentDto createPayment(PaymentDto paymentDto, String creatorEmail) throws ValidationException, ConflictException {
        paymentValidator.validateForCreation(paymentDto, creatorEmail);

        Payment payment = paymentMapper.paymentDtoToPaymentEntity(paymentDto);
        payment.setDate(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);
        Activity activityForExpense = Activity.builder()
            .category(ActivityCategory.PAYMENT)
            .payment(savedPayment)
            .timestamp(LocalDateTime.now())
            .group(savedPayment.getGroup())
            .user(savedPayment.getPayer())
            .build();

        activityRepository.save(activityForExpense);

        return paymentMapper.paymentEntityToPaymentDto(savedPayment);
    }
}
