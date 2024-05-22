package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {
    List<ShoppingList> findAllByGroupId(Long groupId);

    List<ShoppingList> findAllByOwnerId(Long ownerId);

    @Query("select s from ShoppingList s inner join s.group.users users where users.id = ?1")
    List<ShoppingList> findByGroup_Users_Id(Long id);

}