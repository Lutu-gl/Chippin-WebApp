package at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.activity.ActivityDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Activity;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Expense;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public abstract class ActivityMapper {

    @Mapping(target = "expenseId", source = "expense", qualifiedByName = "expenseToExpenseId")
    @Mapping(target = "paymentId", source = "payment", qualifiedByName = "paymentToPaymentId")
    @Mapping(target = "groupId", source = "group", qualifiedByName = "groupToGroupId")
    @Mapping(target = "userId", source = "user", qualifiedByName = "userToUserId")
    public abstract ActivityDetailDto activityEntityToActivityDetailDto(Activity activity);

    @Named("paymentToPaymentId")
    Long expenseToExpenseId(Payment payment) {
        if (payment == null) {
            return null;
        }

        return payment.getId();
    }

    @Named("expenseToExpenseId")
    Long expenseToExpenseId(Expense expense) {
        if (expense == null) {
            return null;
        }

        return expense.getId();
    }

    @Named("groupToGroupId")
    Long groupToGroupId(GroupEntity group) {
        if (group == null) {
            return null;
        }

        return group.getId();
    }

    @Named("userToUserId")
    Long userToUserId(ApplicationUser user) {
        if (user == null) {
            return null;
        }

        return user.getId();
    }

}
