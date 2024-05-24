package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;


import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.UserDetailsDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {

    UserDetailsDto userToUserDetailsDto(ApplicationUser user);
}
