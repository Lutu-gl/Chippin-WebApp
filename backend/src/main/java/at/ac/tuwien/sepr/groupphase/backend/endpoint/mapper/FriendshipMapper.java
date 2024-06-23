package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.friendship.FriendRequestDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Friendship;
import org.mapstruct.Mapper;

@Mapper
public interface FriendshipMapper {
    Friendship friendRequestDtoToFriendship(FriendRequestDto friendRequestDto);
}
