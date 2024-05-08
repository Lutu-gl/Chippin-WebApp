package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import org.mapstruct.Mapper;
import java.util.Set;

@Mapper
public interface GroupMapper {
    Set<GroupDetailDto> setOfGroupEntityToSetOfGroupDto(Set<GroupEntity> groupEntitySet);

    GroupDetailDto groupEntityToGroupDto(GroupEntity groupEntity);
}
