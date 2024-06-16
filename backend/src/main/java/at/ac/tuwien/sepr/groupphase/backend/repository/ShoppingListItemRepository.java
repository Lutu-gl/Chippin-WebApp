package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingListItem;
import at.ac.tuwien.sepr.groupphase.backend.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShoppingListItemRepository extends JpaRepository<ShoppingListItem, Long> {

    @Query("select sum(sli.item.amount) from ShoppingListItem sli left join ShoppingList sl on sli member of sl.items " +
        "where sl.group.id = :groupId and sli.item.description = :description and sli.item.unit = :unit")
    Long getAmountOfItemInGroupShoppingLists(@Param("groupId") Long groupId, @Param("description") String description, @Param("unit") Unit unit);
}