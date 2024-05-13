package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.GroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface GroupMapper {
    Set<GroupDetailDto> setOfGroupEntityToSetOfGroupDto(Set<GroupEntity> groupEntitySet);

    GroupDetailDto groupEntityToGroupDto(GroupEntity groupEntity);

    //@Mapping(target = "users", source = "members", qualifiedByName = "emailsToUsers")
    GroupEntity groupCreateDtoToGroupEntity(GroupCreateDto dto);

    /*
    @Named("emailsToUsers")
    default Set<ApplicationUser> emailsToUsers(Set<String> members, @Context UserRepository userRepository) { // TODO> check again if @Context works. If not discard
        if (members == null) {
            return null;
        }
        return members.stream()
            .map(member -> userRepository.findByEmail(member))  // Direkt ApplicationUser oder null
            .filter(Objects::nonNull)  // Filtere Null-Werte heraus
            .collect(Collectors.toSet());
    }*/

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
