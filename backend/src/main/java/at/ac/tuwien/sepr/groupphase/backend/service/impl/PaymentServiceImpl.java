package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.payment.PaymentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.PaymentMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Activity;
import at.ac.tuwien.sepr.groupphase.backend.entity.ActivityCategory;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.Payment;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ActivityRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PaymentRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.PaymentService;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.PaymentValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    private final PaymentValidator paymentValidator;
    private final PaymentRepository paymentRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentDto createPayment(PaymentDto paymentDto, String creatorEmail) throws ValidationException, ConflictException {
        LOGGER.trace("createPayment({}, {})", paymentDto, creatorEmail);

        paymentValidator.validateForCreation(paymentDto, creatorEmail);

        Payment payment = paymentMapper.paymentDtoToPaymentEntity(paymentDto);
        ApplicationUser user = userRepository.findByEmail(creatorEmail);
        if (!payment.getGroup().getUsers().contains(user)) {
            throw new AccessDeniedException("You do not have permission to create this expense");
        }

        payment.setDate(LocalDateTime.now());
        payment.setDeleted(false);
        payment.setArchived(false);

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

    @Override
    @Transactional
    public PaymentDto getById(long id, String creatorEmail) throws NotFoundException {
        LOGGER.trace("getById({}, {})", id, creatorEmail);

        ApplicationUser user = userRepository.findByEmail(creatorEmail);
        Payment payment = paymentRepository.findById(id).orElseThrow(() -> new NotFoundException("Payment not found"));
        if (!payment.getGroup().getUsers().contains(user)) {
            throw new AccessDeniedException("You do not have permission to access this payment");
        }

        return paymentMapper.paymentEntityToPaymentDto(payment);
    }

    @Override
    @Transactional
    public PaymentDto updatePayment(Long paymentId, PaymentDto paymentDto, String creatorEmail) throws ValidationException, ConflictException, NotFoundException {
        LOGGER.trace("updatePayment({}, {}, {})", paymentId, paymentDto, creatorEmail);

        Payment existingPayment = paymentRepository.findById(paymentId).orElseThrow(() -> new NotFoundException("Payment not found"));
        paymentValidator.validateForUpdate(paymentDto, creatorEmail, existingPayment);

        Payment payment = paymentMapper.paymentDtoToPaymentEntity(paymentDto);
        ApplicationUser user = userRepository.findByEmail(creatorEmail);
        if (!payment.getGroup().getUsers().contains(user)) {
            throw new AccessDeniedException("You do not have permission to access this payment");
        }
        //        if (!existingPayment.getPayer().equals(user) && !existingPayment.getReceiver().equals(user)) {
        //            throw new AccessDeniedException("You do not have permission to update this payment");
        //        }

        payment.setId(paymentId);
        payment.setDate(existingPayment.getDate());
        payment.setArchived(existingPayment.getArchived());
        payment.setDeleted(existingPayment.isDeleted());

        Payment paymentSaved = paymentRepository.save(payment);


        Activity activityForPayment = Activity.builder()
            .category(ActivityCategory.PAYMENT_UPDATE)
            .payment(paymentSaved)
            .timestamp(LocalDateTime.now())
            .group(paymentSaved.getGroup())
            .user(user)
            .build();

        activityRepository.save(activityForPayment);

        return paymentMapper.paymentEntityToPaymentDto(paymentSaved);
    }

    @Override
    @Transactional
    public void deletePayment(Long paymentId, String creatorEmail) throws NotFoundException, ConflictException {
        LOGGER.trace("deletePayment({}, {})", paymentId, creatorEmail);
        Payment existingPayment = paymentRepository.findById(paymentId).orElseThrow(() -> new NotFoundException("Payment not found"));
        ApplicationUser user = userRepository.findByEmail(creatorEmail);

        //        if (!existingPayment.getPayer().equals(user) && !existingPayment.getReceiver().equals(user)) {
        //            throw new AccessDeniedException("You do not have permission to delete this payment");
        //        }
        if (!existingPayment.getGroup().getUsers().contains(user)) {
            throw new AccessDeniedException("You do not have permission to access this payment");
        }

        if (existingPayment.isDeleted()) {
            throw new ConflictException("Invalid delete operation", List.of("Payment is already marked as deleted"));
        }

        paymentRepository.markPaymentAsDeleted(existingPayment);

        Activity activityForPaymentDelete = Activity.builder()
            .category(ActivityCategory.PAYMENT_DELETE)
            .payment(existingPayment)
            .timestamp(LocalDateTime.now())
            .group(existingPayment.getGroup())
            .user(user)
            .build();

        activityRepository.save(activityForPaymentDelete);
    }

    @Override
    @Transactional
    public PaymentDto recoverPayment(Long paymentId, String creatorEmail) throws ConflictException {
        LOGGER.trace("recoverPayment({}, {})", paymentId, creatorEmail);

        Payment existingPayment = paymentRepository.findById(paymentId).orElseThrow(() -> new NotFoundException("Payment not found"));
        final GroupEntity existingGroup = existingPayment.getGroup();
        ApplicationUser user = userRepository.findByEmail(creatorEmail);

        //        if (!existingPayment.getPayer().equals(user) && !existingPayment.getReceiver().equals(user)) {
        //            throw new AccessDeniedException("You do not have permission to recover this payment");
        //        }
        if (!existingPayment.getGroup().getUsers().contains(user)) {
            throw new AccessDeniedException("You do not have permission to access this payment");
        }

        if (!existingPayment.isDeleted()) {
            throw new ConflictException("Invalid recover operation", List.of("Payment is not marked as deleted"));
        }

        paymentRepository.markPaymentAsRecovered(existingPayment);

        existingPayment.setDeleted(false);

        Activity activityForPaymentRecover = Activity.builder()
            .category(ActivityCategory.PAYMENT_RECOVER)
            .payment(existingPayment)
            .timestamp(LocalDateTime.now())
            .group(existingGroup)
            .user(user)
            .build();

        activityRepository.save(activityForPaymentRecover);

        return paymentMapper.paymentEntityToPaymentDto(existingPayment);
    }
}
