package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ActivityDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ActivityMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Activity;
import at.ac.tuwien.sepr.groupphase.backend.entity.ActivityCategory;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ActivityRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandles;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ActivityRepository activityRepository;
    private final ActivityMapper activityMapper;

    @Override
    @Transactional
    public ActivityDetailDto getById(Long id) throws NotFoundException {
        LOGGER.debug("parameters {}", id);

        Activity activityFound = activityRepository.findById(id).orElseThrow(() -> new NotFoundException("Activity not found"));

        ActivityDetailDto activityDetailDto = activityMapper.activityEntityToActivityDetailDto(activityFound);

        activityDetailDto.setDescription(giveDescriptionToActivity(activityFound));

        return activityDetailDto;
    }

    private String giveDescriptionToActivity(Activity activity) {
        switch (activity.getCategory()) {
            case ActivityCategory.EXPENSE:
                return String.format("User %s created expense %s in Group %s", activity.getUser().getEmail(), activity.getExpense().getName(), activity.getGroup().getGroupName());
            default:
                return "No description available";
        }
    }
}
