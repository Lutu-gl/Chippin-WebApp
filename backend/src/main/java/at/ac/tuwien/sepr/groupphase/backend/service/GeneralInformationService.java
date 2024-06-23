package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GeneralInformationDto;

public interface GeneralInformationService {

    /**
    * Get general information about the application.
    *
    * @return the general information
    */
    GeneralInformationDto getGeneralInformation();
}
