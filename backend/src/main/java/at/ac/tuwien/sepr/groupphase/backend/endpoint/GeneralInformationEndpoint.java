package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GeneralInformationDto;
import at.ac.tuwien.sepr.groupphase.backend.service.GeneralInformationService;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/general-information")
@RequiredArgsConstructor
public class GeneralInformationEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final GeneralInformationService generalInformationService;

    @PermitAll
    @GetMapping
    public GeneralInformationDto getGeneralInformation() {
        LOGGER.trace("getGeneralInformation()");
        return generalInformationService.getGeneralInformation();
    }


}
