package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.payment.PaymentDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepr.groupphase.backend.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/payment")
public class PaymentEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final PaymentService paymentService;

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public PaymentDto getById(@PathVariable long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return paymentService.getById(id, authentication.getName());
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PaymentDto createPayment(@Valid @RequestBody PaymentDto paymentDto) throws ValidationException, ConflictException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return paymentService.createPayment(paymentDto, authentication.getName());
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public PaymentDto updatePayment(@PathVariable(name = "id") Long paymentId, @Valid @RequestBody PaymentDto paymentDto) throws ValidationException, ConflictException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return paymentService.updatePayment(paymentId, paymentDto, authentication.getName());
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deletePayment(@PathVariable(name = "id") Long paymentId) throws ConflictException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        paymentService.deletePayment(paymentId, authentication.getName());
    }

    @Secured("ROLE_USER")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/recover/{id}")
    public PaymentDto recoverPayment(@PathVariable(name = "id") Long paymentId) throws ConflictException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return paymentService.recoverPayment(paymentId, authentication.getName());
    }
}
