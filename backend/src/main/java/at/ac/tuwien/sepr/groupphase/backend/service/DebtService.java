package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.debt.DebtGroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;

public interface DebtService {


    DebtGroupDetailDto getById(String userEmail, Long groupId) throws NotFoundException;
}
