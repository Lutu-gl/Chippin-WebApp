package at.ac.tuwien.sepr.groupphase.backend.endpoint;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ItemDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PantryDetailDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.PantrySearchDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.mapper.ItemMapper;
import at.ac.tuwien.sepr.groupphase.backend.entity.Item;
import at.ac.tuwien.sepr.groupphase.backend.service.PantryService;
import jakarta.validation.Valid;
import jakarta.xml.bind.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = "/api/v1/group")
public class PantryEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final PantryService pantryService;
    private final ItemMapper itemMapper;

    @Autowired
    public PantryEndpoint(PantryService pantryService, ItemMapper itemMapper) {
        this.pantryService = pantryService;
        this.itemMapper = itemMapper;
    }

    @Secured("ROLE_USER")
    @GetMapping("/{pantryId}/pantry")
    public PantryDetailDto findAllInPantry(@PathVariable long pantryId) {
        LOGGER.info("GET /api/v1/group/{}/pantry", pantryId);
        return new PantryDetailDto(itemMapper.listOfItemsToListOfItemDetailDto(pantryService.findAllItems(pantryId)));
    }

    @Secured("ROLE_USER")
    @GetMapping("/{pantryId}/pantry/search")
    public PantryDetailDto searchItemsInPantry(@PathVariable long pantryId, PantrySearchDto searchParams) {
        LOGGER.info("GET /api/v1/group/{}/pantry/search", pantryId);
        LOGGER.debug("request parameters: {}", searchParams);
        return new PantryDetailDto(itemMapper.listOfItemsToListOfItemDetailDto(pantryService.findItemsByDescription(searchParams.getDetails(), pantryId)));
    }

    @Secured("ROLE_USER")
    @PostMapping("/{pantryId}/pantry")
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto addItemToPantry(@PathVariable long pantryId, @Valid @RequestBody ItemDetailDto itemDetailDto) throws ValidationException {
        LOGGER.info("POST /api/v1/group/{}/pantry body: {}", pantryId, itemDetailDto);
        Item item = itemMapper.itemDetailDtoToItem(itemDetailDto);
        return itemMapper.itemToItemDto(pantryService.addItemToPantry(item, pantryId));
    }
}
