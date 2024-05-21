package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.activity.ActivityDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ActivityMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Activity;
import at.ac.tuwien.sepr.groupphase.backend.entity.ActivityCategory;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ActivityRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.LinkedList;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ActivityRepository activityRepository;
    private final ActivityMapper activityMapper;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ActivityDetailDto getById(Long id) throws NotFoundException {
        LOGGER.debug("parameters {}", id);
        Activity activityFound = activityRepository.findById(id).orElseThrow(() -> new NotFoundException("Activity not found"));
        ActivityDetailDto activityDetailDto = activityMapper.activityEntityToActivityDetailDto(activityFound);
        activityDetailDto.setDescription(giveDescriptionToActivity(activityFound));
        return activityDetailDto;
    }

    @Override
    @Transactional
    public Collection<ActivityDetailDto> getExpenseActivitiesByGroupId(Long groupId, String requesterEmail) throws NotFoundException {
        LOGGER.debug("parameters {} {}", groupId, requesterEmail);

        ApplicationUser user = userRepository.findByEmail(requesterEmail);
        GroupEntity group = groupRepository.findById(groupId).orElseThrow(() -> new NotFoundException("Group not found"));
        if (!group.getUsers().contains(user)) {
            throw new AccessDeniedException("Authenticated user is not allowed to access this group!");
        }
        Collection<Activity> activitiesFound = activityRepository.findExpenseActivitiesByGroup(group);

        Collection<ActivityDetailDto> activityDetailDtos = new LinkedList<>();
        for (Activity activity : activitiesFound) {
            ActivityDetailDto activityDetailDto = activityMapper.activityEntityToActivityDetailDto(activity);
            activityDetailDto.setDescription(giveDescriptionToActivity(activity));
            activityDetailDtos.add(activityDetailDto);
        }

        return activityDetailDtos;
    }


    private String giveDescriptionToActivity(Activity activity) {
        return switch (activity.getCategory()) {
            case ActivityCategory.EXPENSE ->
                String.format("User %s created expense %s in group %s", activity.getUser().getEmail(), activity.getExpense().getName(), activity.getGroup().getGroupName());
            case ActivityCategory.EXPENSE_UPDATE ->
                String.format("User %s updated expense %s in group %s", activity.getUser().getEmail(), activity.getExpense().getName(), activity.getGroup().getGroupName());
            case ActivityCategory.EXPENSE_DELETE ->
                String.format("User %s deleted expense %s in group %s", activity.getUser().getEmail(), activity.getExpense().getName(), activity.getGroup().getGroupName());
            case ActivityCategory.EXPENSE_RECOVER ->
                String.format("User %s recovered expense %s in group %s", activity.getUser().getEmail(), activity.getExpense().getName(), activity.getGroup().getGroupName());
            default -> "No description available";
        };
    }
}