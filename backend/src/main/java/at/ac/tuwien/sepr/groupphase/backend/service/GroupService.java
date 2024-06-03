package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.group.GroupCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

public interface GroupService {


    /**
     * Creates new group in the persistent data store.
     * Assigns a new not used ID to the group
     *
     * @param groupCreateDto the parameters to create a new Group
     * @param ownerEmail     email of the owner that send the request
     * @return the new created group
     * @throws ValidationException if the provided data is invalid
     * @throws ConflictException   entity in the request itself is valid, but still cannot be accepted due to the overall status.
     */
    GroupCreateDto create(GroupCreateDto groupCreateDto, String ownerEmail) throws ValidationException, ConflictException;

    /**
     * Updates the group with the ID given in {@code groupCreateDto}.
     *
     * @param groupCreateDto the group to update.
     * @param ownerEmail     email of the owner that send the request
     * @return the updated group
     * @throws ValidationException if the provided data is invalid
     * @throws ConflictException   entity in the request itself is valid, but still cannot be accepted due to the overall status.
     * @throws NotFoundException   if the group with given ID does not exist in the persistent data store
     */
    GroupCreateDto update(GroupCreateDto groupCreateDto, String ownerEmail) throws ValidationException, ConflictException, NotFoundException;


    /**
     * Get the group with given ID, with more detail information.
     *
     * @param id the ID of the group to get
     * @return the group with ID {@code id}
     * @throws NotFoundException if the group with the given ID does not exist in the persistent data store
     */
    GroupCreateDto getById(long id) throws NotFoundException;
}
