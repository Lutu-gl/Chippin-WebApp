package at.ac.tuwien.sepr.groupphase.backend.unittests.servicetests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTest;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ShoppingListCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ShoppingListMapperImpl;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.ShoppingListServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class ShoppingListServiceTest extends BaseTest {

    @Mock
    private ShoppingListRepository shoppingListRepository;

    @Spy
    private ShoppingListMapperImpl shoppingListMapper;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private ShoppingListServiceImpl shoppingListService;

    @Test
    @Transactional
    @Rollback
    public void givenValidShoppingListCreateDto_whenCreateShoppingList_thenNoException() {
        when(shoppingListRepository.save(any())).thenReturn(new ShoppingList());
        when(groupRepository.findById(any())).thenReturn(Optional.of(GroupEntity.builder().id(-1L).build()));
        var shoppingListCreateDto = ShoppingListCreateDto.builder()
            .name("Test Shopping List")
            .build();

        shoppingListService.createShoppingList(shoppingListCreateDto, -1L);

        verify(shoppingListRepository, times(1)).save(any());
    }

    @Test
    @Transactional
    @Rollback
    public void givenValidShoppingListCreateDtoWithInvalidGroupId_whenCreateShoppingList_thenNotFoundException() {
        when(groupRepository.findById(any())).thenReturn(Optional.empty());
        var shoppingListCreateDto = ShoppingListCreateDto.builder()
            .name("Test Shopping List")
            .build();
        assertThrows(NotFoundException.class, () -> shoppingListService.createShoppingList(shoppingListCreateDto, -1L));

        verify(shoppingListRepository, times(0)).save(any());
    }

    @Test
    @Transactional
    @Rollback
    public void givenValidShoppingListId_whenGetShoppingList_thenNoException() {
        when(shoppingListRepository.findById(any())).thenReturn(Optional.of(new ShoppingList()));

        shoppingListService.getShoppingList(-1L);

        verify(shoppingListRepository, times(1)).findById(any());
    }

    @Test
    @Transactional
    @Rollback
    public void givenValidShoppingListId_whenDeleteShoppingList_thenNoException() {
        shoppingListService.deleteShoppingList(-1L);

        verify(shoppingListRepository, times(1)).deleteById(any());
    }

    @Test
    @Transactional
    @Rollback
    public void givenValidGroupId_whenGetShoppingListsForGroup_thenNoException() {
        when(shoppingListRepository.findAllByGroupId(any())).thenReturn(List.of());

        shoppingListService.getShoppingListsForGroup(-1L);

        verify(shoppingListRepository, times(1)).findAllByGroupId(any());
    }

}
