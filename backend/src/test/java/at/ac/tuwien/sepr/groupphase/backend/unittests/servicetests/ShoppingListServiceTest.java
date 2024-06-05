package at.ac.tuwien.sepr.groupphase.backend.unittests.servicetests;

import at.ac.tuwien.sepr.groupphase.backend.basetest.BaseTestGenAndClearBevorAfterEach;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.group.GroupDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.shoppinglist.ShoppingListCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ShoppingListMapperImpl;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.GroupEntity;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingList;
import at.ac.tuwien.sepr.groupphase.backend.entity.ShoppingListItem;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepr.groupphase.backend.repository.GroupRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.ShoppingListRepository;
import at.ac.tuwien.sepr.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.impl.ShoppingListServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class ShoppingListServiceTest extends BaseTestGenAndClearBevorAfterEach {

    @Mock
    private ShoppingListRepository shoppingListRepository;

    @Spy
    private ShoppingListMapperImpl shoppingListMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private ShoppingListServiceImpl shoppingListService;

    @Test
    public void givenValidShoppingListCreateDtoWithoutGroupId_whenCreateShoppingList_thenNoException() {
        when(shoppingListRepository.save(any())).thenReturn(new ShoppingList());
        when(groupRepository.findById(any())).thenReturn(Optional.of(GroupEntity.builder().id(-1L).build()));
        when(userRepository.findById(any())).thenReturn(Optional.of(ApplicationUser.builder().id(-1L).build()));
        var shoppingListCreateDto = ShoppingListCreateDto.builder()
            .name("Test Shopping List")
            .build();

        shoppingListService.createShoppingList(shoppingListCreateDto, -1L);

        verify(shoppingListRepository, times(1)).save(any());
    }

    @Test
    public void givenValidShoppingListCreateDtoWithValidGroupId_whenCreateShoppingList_thenNoException() {
        when(shoppingListRepository.save(any())).thenReturn(new ShoppingList());
        when(groupRepository.findById(any())).thenReturn(Optional.of(GroupEntity.builder().id(-1L).build()));
        when(userRepository.findById(any())).thenReturn(Optional.of(ApplicationUser.builder().id(-1L).build()));
        var shoppingListCreateDto = ShoppingListCreateDto.builder()
            .name("Test Shopping List")
            .group(GroupDetailDto.builder().id(-1L).build())
            .build();

        shoppingListService.createShoppingList(shoppingListCreateDto, -1L);

        verify(shoppingListRepository, times(1)).save(any());
    }

    @Test
    public void givenValidShoppingListCreateDtoWithInvalidGroupId_whenCreateShoppingList_thenNotFoundException() {
        when(groupRepository.findById(any())).thenReturn(Optional.empty());
        when(userRepository.findById(any())).thenReturn(Optional.of(ApplicationUser.builder().id(-1L).build()));
        var shoppingListCreateDto = ShoppingListCreateDto.builder()
            .name("Test Shopping List")
            .group(GroupDetailDto.builder().id(-1L).build())
            .build();
        assertThrows(NotFoundException.class, () -> shoppingListService.createShoppingList(shoppingListCreateDto, -1L));

        verify(shoppingListRepository, times(0)).save(any());
    }

    @Test
    public void givenValidShoppingListId_whenGetShoppingList_thenNoException() {
        when(shoppingListRepository.findById(any())).thenReturn(Optional.of(new ShoppingList()));

        shoppingListService.getShoppingList(-1L);

        verify(shoppingListRepository, times(1)).findById(any());
    }

    @Test
    public void givenValidShoppingListId_whenDeleteShoppingList_thenNoException() throws ConflictException {
        shoppingListService.deleteShoppingList(-1L);

        verify(shoppingListRepository, times(1)).deleteById(any());
    }

    @Test
    public void givenValidGroupId_whenGetShoppingListsForGroup_thenNoException() {
        when(shoppingListRepository.findAllByGroupId(any())).thenReturn(List.of());

        shoppingListService.getShoppingListsForGroup(-1L);

        verify(shoppingListRepository, times(1)).findAllByGroupId(any());
    }

    @Test
    public void givenValidUserId_whenGetShoppingListsForUser_thenNoException() {
        when(shoppingListRepository.findAllByOwnerId(any())).thenReturn(new ArrayList<>());
        when(shoppingListRepository.findByGroup_Users_Id(any())).thenReturn(new ArrayList<>());

        shoppingListService.getShoppingListsForUser(-1L);

        verify(shoppingListRepository, times(1)).findAllByOwnerId(any());
    }


    @Test
    public void givenValidShoppingListId_whenDeleteCheckedItems_thenNoException() {
        Long shoppingListId = -1L;
        var items = new ArrayList<ShoppingListItem>(){
            {
                add(ShoppingListItem.builder().id(1L).checkedBy(new ApplicationUser()).build());
                add(ShoppingListItem.builder().id(2L).checkedBy(new ApplicationUser()).build());
                add(ShoppingListItem.builder().id(3L).build());
            }
        };
        ShoppingList shoppingList = ShoppingList.builder()
            .id(shoppingListId)
            .items(items)
            .build();
        when(shoppingListRepository.findById(shoppingListId)).thenReturn(Optional.of(shoppingList));

        shoppingListService.deleteCheckedItems(-1L);

        verify(shoppingListRepository, times(1)).findById(shoppingListId);
        verify(shoppingListRepository, times(1)).save(any());
        assertAll(
            () -> assertThat(shoppingList.getItems()).hasSize(1),
            () -> assertThat(shoppingList.getItems().getFirst().getId()).isEqualTo(3L)
        );
    }

    @Test
    public void givenInvalidShoppingListId_whenDeleteCheckedItems_thenNotFoundException() {
        when(shoppingListRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> shoppingListService.deleteCheckedItems(-1L));

        verify(shoppingListRepository, times(1)).findById(-1L);
    }
}
