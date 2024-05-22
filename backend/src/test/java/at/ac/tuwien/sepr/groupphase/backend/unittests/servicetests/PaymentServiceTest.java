package at.ac.tuwien.sepr.groupphase.backend.unittests.servicetests;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.payment.PaymentDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.PaymentMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Payment;
import at.ac.tuwien.sepr.groupphase.backend.repository.ActivityRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.PaymentRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.PaymentServiceImpl;
import at.ac.tuwien.sepr.groupphase.backend.service.validator.PaymentValidator;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@SpringBootTest
@AutoConfigureMockMvc
public class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private PaymentValidator paymentValidator;
    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    public void testCreateExpenseSuccess() throws Exception {
        // Mock-Konfigurationen
        PaymentDto paymentDto = PaymentDto.builder()
            .amount(10.0)
            .payerEmail("user1@example.com")
            .receiverEmail("user2@example.com")
            .groupId(1L)
            .build();

        Payment mockPaymentEntity = Payment.builder().build();

        when(paymentMapper.paymentDtoToPaymentEntity(paymentDto)).thenReturn(mockPaymentEntity);
        when(paymentRepository.save(any(Payment.class))).thenReturn(mockPaymentEntity);
        when(userRepository.findByEmail(anyString())).thenReturn(ApplicationUser.builder().email("test@email.com").build());
        when(paymentMapper.paymentEntityToPaymentDto(any(Payment.class)))
            .thenReturn(PaymentDto.builder()
                .amount(10.0)
                .payerEmail("user1@example.com")
                .receiverEmail("user2@example.com")
                .groupId(1L)
                .build());

        // Execution
        PaymentDto result = paymentService.createPayment(paymentDto, "test@email.com");

        // Verification
        assertNotNull(result);
        assertEquals(10.0, result.getAmount());
        assertEquals("user1@example.com", result.getPayerEmail());
        assertEquals("user2@example.com", result.getReceiverEmail());
        assertEquals(1L, result.getGroupId());
        verify(paymentValidator, times(1)).validateForCreation(paymentDto, "test@email.com");
        verify(activityRepository, times(1)).save(any());
    }
}
