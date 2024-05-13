package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemCreateDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemListDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemListListDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemListSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.service.ItemListService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/group")
public class ItemListEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ItemListService itemListService;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemListEndpoint(ItemListService itemListService, ItemMapper itemMapper) {
        this.itemListService = itemListService;
        this.itemMapper = itemMapper;
    }


    @Secured("ROLE_USER")
    @GetMapping("/{itemListId}/itemlist")
    public ItemListDetailDto getById(@PathVariable long itemListId) {
        LOGGER.info("GET /api/v1/group/{}/itemlist", itemListId);
        return new ItemListDetailDto(itemMapper.listOfItemsToListOfItemDto(itemListService.findAllItems(itemListId)), itemListService.getName(itemListId));
    }

    @Secured("ROLE_USER")
    @GetMapping("/{itemListId}/itemlist/search")
    public ItemListListDto searchItemsInItemList(@PathVariable long itemListId, ItemListSearchDto searchParams) {
        LOGGER.info("GET /api/v1/itemlist/{}/itemlist/search", itemListId);
        LOGGER.debug("request parameters: {}", searchParams);
        return new ItemListListDto(itemMapper.listOfItemsToListOfItemDto(itemListService.findItemsByDescription(searchParams.getDetails(), itemListId)));
    }

    @Secured("ROLE_USER")
    @PostMapping("/{itemListId}/itemlist")
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addItemToPantry(@PathVariable long itemListId, @Valid @RequestBody ItemCreateDto itemCreateDto) {
        LOGGER.info("POST /api/v1/group/{}/itemlist body: {}", itemListId, itemCreateDto);
        Item item = itemMapper.itemCreateDtoToItem(itemCreateDto);
        return itemMapper.itemToItemDto(itemListService.addItemToItemList(item, itemListId));
    }

    @Secured("ROLE_USER")
    @DeleteMapping("/{itemListId}/itemlist/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable long itemListId, @PathVariable long itemId) {
        LOGGER.info("DELETE /api/v1/group/{}/pantry/{}", itemListId, itemId);
        itemListService.deleteItem(itemListId, itemId);
    }

    @Secured("ROLE_USER")
    @PutMapping("/{itemListId}/itemlist")
    public ItemDto updateItem(@PathVariable long itemListId, @Valid @RequestBody ItemDto itemDto) {
        LOGGER.info("PUT /api/v1/group/{}/itemlist body: {}", itemListId, itemDto);
        return itemMapper.itemToItemDto(itemListService.updateItem(itemDto, itemListId));
    }
}
