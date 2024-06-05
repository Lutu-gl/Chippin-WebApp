package at.ac.tuwien.sepr.groupphase.backend.service.validator;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListUpdateDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ShoppingListValidator {

    private final ShoppingListRepository shoppingListRepository;

    public void validateForUpdateGroup(ShoppingListUpdateDto shoppingListUpdateDto, ShoppingList shoppingListEntity) throws ConflictException {
        if (shoppingListUpdateDto.getGroup().getId().equals(shoppingListEntity.getGroup().getId())) {
            return;
        }
        // Validate user is owner of shopping list
        if (shoppingListEntity.getOwner().getId() != SecurityContextHolder.getContext().getAuthentication().getPrincipal()){
            throw new ConflictException("Conflict error", List.of("User is not owner of shopping list. Only the owner can update a shopping list."));
        }
    }

    public void validateForDelete(Long id) throws ConflictException {
        // Get shopping list by id
        var shoppingList = shoppingListRepository.findById(id).orElse(null);
        if (shoppingList == null) return;
        // Validate user is owner of shopping list
        if (shoppingList.getOwner().getId() != SecurityContextHolder.getContext().getAuthentication().getPrincipal()){
            throw new ConflictException("Conflict error", List.of("User is not owner of shopping list. Only the owner can delete a shopping list."));
        }

    }
}
