package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.group.GroupCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.expense.ExpenseCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.expense.ExpenseDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class ExpenseMapper {

    @Autowired
    GroupRepository groupRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    GroupMapper groupMapper;

    //abstract Set<GroupDetailDto> setOfGroupEntityToSetOfGroupDto(Set<GroupEntity> groupEntitySet);

    //abstract GroupDetailDto groupEntityToGroupDto(GroupEntity groupEntity);

    @Mapping(target = "payer", source = "payerEmail", qualifiedByName = "emailsToUser")
    @Mapping(target = "group", source = "groupId", qualifiedByName = "groupIdToGroup")
    @Mapping(target = "participants", source = "participants", qualifiedByName = "participantsEmailToApplicationUser")
    public abstract Expense expenseCreateDtoToExpenseEntity(ExpenseCreateDto dto);


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

    @Named("participantsEmailToApplicationUser")
    Map<ApplicationUser, Double> participantsEmailToApplicationUser(Map<String, Double> participants) {
        if (participants == null) {
            return null;
        }

        return participants.entrySet().stream().collect(Collectors.toMap(entry -> userRepository.findByEmail(entry.getKey()), Map.Entry::getValue));
    }

    @Mapping(target = "payerEmail", source = "payer", qualifiedByName = "usersToEmail")
    @Mapping(target = "groupId", source = "group", qualifiedByName = "groupToGroupId")
    @Mapping(target = "participants", source = "participants", qualifiedByName = "applicationUserToParticipantsEmail")
    public abstract ExpenseCreateDto expenseEntityToExpenseCreateDto(Expense expenseSaved);

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

    @Named("applicationUserToParticipantsEmail")
    Map<String, Double> applicationUserToParticipantsEmail(Map<ApplicationUser, Double> participants) {
        if (participants == null) {
            return null;
        }
        return participants.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().getEmail(), Map.Entry::getValue));
    }

    @Mapping(target = "payerEmail", source = "payer", qualifiedByName = "usersToEmail")
    @Mapping(target = "group", source = "group", qualifiedByName = "groupEntityToGroupDetailDto")
    @Mapping(target = "participants", source = "participants", qualifiedByName = "applicationUserToParticipantsEmail")
    public abstract ExpenseDetailDto expenseEntityToExpenseDetailDto(Expense expense);

    @Named("groupEntityToGroupDetailDto")
    GroupCreateDto groupEntityToGroupCreateDto(GroupEntity group) {
        if (group == null) {
            return null;
        }
        return groupMapper.groupEntityToGroupCreateDto(group);
    }
}
