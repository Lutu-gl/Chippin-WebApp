package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

public interface GroupService {


    /**
     * Creates new group in the persistent data store.
     * Assigns a new not used ID to the group
     *
     * @param groupCreateDto the parameters to create a new Group
     * @param ownerEmail email of the owner that send the request
     * @return the new created group
     * @throws ValidationException if the provided data is invalid
     * @throws ConflictException entity in the request itself is valid, but still cannot be accepted due to the overall status.
     */
    GroupCreateDto create(GroupCreateDto groupCreateDto, String ownerEmail) throws ValidationException, ConflictException;
}
