package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.activity.ActivityDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;

import java.util.Collection;

public interface ActivityService {

    /**
     * Get a specific activity by id.
     *
     * @param id the id of the activity
     * @return the activity
     * @throws NotFoundException if the activity is not found
     */
    ActivityDetailDto getById(Long id) throws NotFoundException;

    /**
     * Get all expense activities of a specific group.
     *
     * @param groupId        the id of the group
     * @param requesterEmail the email of the user who requests the activities
     * @return a collection of the expense activities
     * @throws NotFoundException if the group is not found
     */
    Collection<ActivityDetailDto> getExpenseActivitiesByGroupId(Long groupId, String requesterEmail) throws NotFoundException;
}
