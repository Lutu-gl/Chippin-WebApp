package at.ac.tuwien.sepr.groupphase.backend.datagenerator;

import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile("generateData")
@Component
@AllArgsConstructor
public class ShoppingListDataGenerator {

    private final ShoppingListRepository shoppingListRepository;
    private final GroupRepository groupRepository;

    @PostConstruct
    private void generateData() {

        // Genereate a group
        var group = groupRepository.save(
            GroupEntity.builder()
                .groupName("ChippIn")
                .build()
        );

        for (int i = 0; i < 10; i++) {
            var shoppingList = ShoppingList.builder()
                .name("ShoppingList" + i)
                .budget(100.0f)
                .group(group)
                .items(List.of())
                .build();
            shoppingListRepository.save(shoppingList);
        }
    }
}