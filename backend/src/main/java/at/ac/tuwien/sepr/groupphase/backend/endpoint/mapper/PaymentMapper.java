package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.payment.PaymentDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.Payment;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class PaymentMapper {

    @Autowired
    GroupRepository groupRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    GroupMapper groupMapper;


    @Mapping(target = "payer", source = "payerEmail", qualifiedByName = "emailsToUser")
    @Mapping(target = "receiver", source = "receiverEmail", qualifiedByName = "emailsToUser")
    @Mapping(target = "group", source = "groupId", qualifiedByName = "groupIdToGroup")
    public abstract Payment paymentDtoToPaymentEntity(PaymentDto dto);


    @Named("emailsToUser")
    ApplicationUser emailsToUser(String payerEmail) {
        if (payerEmail == null) {
            return null;
        }

        return userRepository.findByEmail(payerEmail);
    }

    @Named("groupIdToGroup")
    GroupEntity groupIdToGroup(Long groupId) {
        if (groupId == null) {
            return null;
        }

        return groupRepository.findById(groupId).orElse(null);
    }

    @Mapping(target = "payerEmail", source = "payer", qualifiedByName = "usersToEmail")
    @Mapping(target = "receiverEmail", source = "receiver", qualifiedByName = "usersToEmail")
    @Mapping(target = "groupId", source = "group", qualifiedByName = "groupToGroupId")
    public abstract PaymentDto paymentEntityToPaymentDto(Payment payment);

    @Named("usersToEmail")
    String usersToEmail(ApplicationUser payer) {
        if (payer == null) {
            return null;
        }
        return payer.getEmail();
    }

    @Named("groupToGroupId")
    Long groupToGroupId(GroupEntity group) {
        if (group == null) {
            return null;
        }
        return group.getId();
    }
}
