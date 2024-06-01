package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.group.GroupCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.group.GroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface GroupMapper {
    Set<GroupDetailDto> setOfGroupEntityToSetOfGroupDto(Set<GroupEntity> groupEntitySet);

    GroupDetailDto groupEntityToGroupDto(GroupEntity groupEntity);

    //@Mapping(target = "users", source = "members", qualifiedByName = "emailsToUsers")
    GroupEntity groupCreateDtoToGroupEntity(GroupCreateDto dto);

    @Mapping(target = "members", source = "users", qualifiedByName = "usersToEmails")
    GroupCreateDto groupEntityToGroupCreateDto(GroupEntity entity);

    @Named("usersToEmails")
    default Set<String> usersToEmails(Set<ApplicationUser> users) {
        if (users == null) {
            return null;
        }
        return users.stream().map(ApplicationUser::getEmail).collect(Collectors.toSet());
    }
}
