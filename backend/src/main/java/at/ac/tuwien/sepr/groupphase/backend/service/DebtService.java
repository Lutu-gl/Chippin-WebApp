package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.debt.DebtGroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;

public interface DebtService {


    /**
     * Get debts of a group from viewpoint of a user.
     *
     * @param userEmail viewpoint user
     * @param groupId   group id
     * @return DebtGroupDetailDto that describes the relationship between the user and the debts to the other users
     * @throws NotFoundException if the group is not found
     */
    DebtGroupDetailDto getById(String userEmail, Long groupId) throws NotFoundException;
}
