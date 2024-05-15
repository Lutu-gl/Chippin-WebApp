package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ActivityDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;

public interface ActivityService {

    ActivityDetailDto getById(Long id) throws NotFoundException;
}
